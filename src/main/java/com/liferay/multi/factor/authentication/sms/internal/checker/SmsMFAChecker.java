/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.multi.factor.authentication.sms.internal.checker;

import com.liferay.multi.factor.authentication.sms.internal.configuration.MFASmsOTPConfiguration;
import com.liferay.multi.factor.authentication.sms.internal.constans.MFASmsOTPWebKeys;
import com.liferay.multi.factor.authentication.spi.checker.browser.BrowserMFAChecker;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marta Medio
 */
@Component(
	configurationPid = "com.liferay.multi.factor.authentication.sms.internal.configuration.MFASmsOTPConfiguration.scoped",
	configurationPolicy = ConfigurationPolicy.OPTIONAL, service = {}
)
public class SmsMFAChecker implements BrowserMFAChecker {

	@Override
	public void includeBrowserVerification(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long userId)
		throws Exception {

		User user = _userLocalService.fetchUser(userId);

		if (user == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Requested one-time password email verification for " +
						"nonexistent user " + userId);
			}

			return;
		}

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		HttpSession httpSession = originalHttpServletRequest.getSession();

		httpSession.setAttribute(MFASmsOTPWebKeys.MFA_SMS_OTP_USER_ID, userId);

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher(
				"/mfa_sms_checker/verify_browser.jsp");

		requestDispatcher.include(httpServletRequest, httpServletResponse);
	}

	@Override
	public boolean isAvailable(long userId) {
		User user = _userLocalService.fetchUser(userId);

		if (!user.getPhones().isEmpty()) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isBrowserVerified(
		HttpServletRequest httpServletRequest, long userId) {

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		HttpSession httpSession = originalHttpServletRequest.getSession(false);

		if (httpSession.getAttribute("mfa-sms-validated") != null) {
			return true;
		}

		return false;
	}

	@Override
	public boolean verifyBrowserRequest(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, long userId)
		throws Exception {

		String mfaSmsCode = ParamUtil.getString(
			httpServletRequest, "mfaSmsCode");

		if (Validator.isBlank(mfaSmsCode)) {
			return false;
		}

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		HttpSession httpSession = originalHttpServletRequest.getSession();

		String smsCode = (String)httpSession.getAttribute(
			MFASmsOTPWebKeys.MFA_SMS_OTP);

		if (mfaSmsCode.equals(smsCode)) {
			httpSession.setAttribute("mfa-sms-validated", Boolean.TRUE);

			return true;
		}

		return false;
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		MFASmsOTPConfiguration mfaSmsOTPConfiguration =
			ConfigurableUtil.createConfigurable(
				MFASmsOTPConfiguration.class, properties);

		if (!mfaSmsOTPConfiguration.enabled()) {
			return;
		}

		_serviceRegistration = bundleContext.registerService(
			BrowserMFAChecker.class, this, new HashMapDictionary<>(properties));
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceRegistration == null) {
			return;
		}

		_serviceRegistration.unregister();
	}

	private static final Log _log = LogFactoryUtil.getLog(SmsMFAChecker.class);

	@Reference
	private Portal _portal;

	private ServiceRegistration<?> _serviceRegistration;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.multi.factor.authentication.sms.otp.web)"
	)
	private ServletContext _servletContext;

	@Reference
	private UserLocalService _userLocalService;

}