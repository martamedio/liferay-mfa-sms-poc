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

package com.liferay.multi.factor.authentication.sms.internal.portlet.action;

import com.liferay.multi.factor.authentication.sms.internal.configuration.MFASmsOTPConfiguration;
import com.liferay.multi.factor.authentication.sms.internal.constans.MFASmsOTPWebKeys;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.AuthToken;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;

import java.util.Arrays;
import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marta Medio
 */
@Component(
	property = {
		"javax.portlet.name=" + MFASmsOTPWebKeys.MFA_SMS_OTP_VERIFY,
		"mvc.command.name=/mfa_sms_otp_verify/send_mfa_sms_otp"
	},
	service = MVCResourceCommand.class
)
public class SendMFASmsMVCResourceCommand implements MVCResourceCommand {

	@Override
	public boolean serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(resourceRequest));

		try {
			_authToken.checkCSRFToken(
				httpServletRequest,
				SendMFASmsMVCResourceCommand.class.getName());
		}
		catch (PrincipalException principalException) {
			throw new PortletException(principalException);
		}

		try {
			return _serveResource(httpServletRequest);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	private String _generateSendSmsCode(String apiUrl, User user)
		throws Exception {

		HttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(apiUrl);

		List<BasicNameValuePair> params = Arrays.asList(
			new BasicNameValuePair("phone", _getPhoneNumber(user)));

		httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

		HttpResponse response = httpClient.execute(httpPost);

		HttpEntity entity = response.getEntity();

		if (entity != null) {
			return EntityUtils.toString(response.getEntity(), "UTF-8");
		}

		return null;
	}

	private String _getPhoneNumber(User user) {
		return user.getPhones().stream().findFirst().get().getNumber();
	}

	private boolean _serveResource(HttpServletRequest httpServletRequest)
		throws Exception {

		HttpSession httpSession = httpServletRequest.getSession();

		Long mfaEmailOTPUserId = (Long)httpSession.getAttribute(
			MFASmsOTPWebKeys.MFA_SMS_OTP_USER_ID);

		if (mfaEmailOTPUserId == null) {
			if (_log.isWarnEnabled()) {
				_log.warn("User ID is not in the session");
			}

			return false;
		}

		User user = _userLocalService.getUserById(mfaEmailOTPUserId);

		MFASmsOTPConfiguration mfaSmsOTPConfiguration =
			_configurationProvider.getCompanyConfiguration(
				MFASmsOTPConfiguration.class, user.getCompanyId());

		if (mfaSmsOTPConfiguration == null) {
			return false;
		}

		String generatedSmsCode = _generateSendSmsCode(
			mfaSmsOTPConfiguration.apiUrl(), user);

		httpSession.setAttribute(
			MFASmsOTPWebKeys.MFA_SMS_OTP, generatedSmsCode);

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SendMFASmsMVCResourceCommand.class);

	@Reference
	private AuthToken _authToken;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}