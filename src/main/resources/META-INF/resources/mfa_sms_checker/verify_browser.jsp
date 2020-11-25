<%--
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
--%>

<%@ include file="/init.jsp" %>

<liferay-portlet:resourceURL id="/mfa_sms_otp_verify/send_mfa_sms_otp" portletName="<%= MFASmsOTPWebKeys.MFA_SMS_OTP_VERIFY %>" var="sendSmsOTPUrl" />

<div>
	<div class="portlet-msg-info">
		<liferay-ui:message key="press-the-button-below-to-obtain-your-sms-one-time-password-it-will-be-sent-to-your-phone" />
	</div>

	<aui:button-row>
		<clay:button
			additionalProps='<%=
				HashMapBuilder.<String, Object>put(
					"url", sendSmsOTPUrl
				).build()
			%>'
			id="mfaSendSmsButton"
			label="send"
			propsTransformer="js/propsTransformer"
		/>
	</aui:button-row>
</div>

<div>
	<aui:input autocomplete="off" label="enter-the-received-sms-code" name="mfaSmsCode" showRequiredLabel="yes" />
</div>

<aui:button-row>
	<aui:button id="submitSmsButton" type="submit" value="submit" />
</aui:button-row>