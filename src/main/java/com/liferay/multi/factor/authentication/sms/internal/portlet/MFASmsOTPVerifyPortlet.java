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

package com.liferay.multi.factor.authentication.sms.internal.portlet;

import com.liferay.multi.factor.authentication.sms.internal.constans.MFASmsOTPWebKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.auth.InterruptedPortletRequestWhitelistUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.util.PropsValues;

import javax.portlet.Portlet;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marta Medio
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.application-type=full-page-application",
		"com.liferay.portlet.css-class-wrapper=portlet-mfa-sms-otp-verify",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.preferences-company-wide=true",
		"javax.portlet.display-name=Multi Factor Authentication SMS One-Time Password Verify",
		"javax.portlet.init-param.mvc-command-names-default-views=/mfa_sms_otp_verify/verify",
		"javax.portlet.init-param.portlet-title-based-navigation=true",
		"javax.portlet.init-param.template-path=/META-INF/resources/mfa_sms_otp_verify/",
		"javax.portlet.name=" + MFASmsOTPWebKeys.MFA_SMS_OTP_VERIFY,
		"javax.portlet.resource-bundle=content.Language",
		"portlet.add.default.resource.check.whitelist=" + MFASmsOTPWebKeys.MFA_SMS_OTP_VERIFY
	},
	service = Portlet.class
)
public class MFASmsOTPVerifyPortlet extends MVCPortlet {

	@Activate
	protected void activate(BundleContext bundleContext) {
		PropsValues.PORTLET_INTERRUPTED_REQUEST_WHITELIST = ArrayUtil.append(
			PropsValues.PORTLET_INTERRUPTED_REQUEST_WHITELIST,
			MFASmsOTPWebKeys.MFA_SMS_OTP_VERIFY);

		_interruptedPortletRequestWhitelistUtil.
			resetPortletInvocationWhitelist();
	}

	@Deactivate
	protected void deactivate() {
		PropsValues.PORTLET_INTERRUPTED_REQUEST_WHITELIST = ArrayUtil.remove(
			PropsValues.PORTLET_INTERRUPTED_REQUEST_WHITELIST,
			MFASmsOTPWebKeys.MFA_SMS_OTP_VERIFY);

		_interruptedPortletRequestWhitelistUtil.
			resetPortletInvocationWhitelist();
	}

	@Reference
	private InterruptedPortletRequestWhitelistUtil
		_interruptedPortletRequestWhitelistUtil;

}