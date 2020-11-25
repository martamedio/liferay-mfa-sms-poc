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

package com.liferay.multi.factor.authentication.sms.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Marta Medio
 */
@ExtendedObjectClassDefinition(
	category = "multi-factor-authentication",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.multi.factor.authentication.sms.internal.configuration.MFASmsOTPConfiguration",
	localization = "content/Language", name = "mfa-sms-configuration-name"
)
public interface MFASmsOTPConfiguration {

	@Meta.AD(deflt = "false", name = "enabled", required = false)
	public boolean enabled();

	@Meta.AD(
		deflt = "400", id = "service.ranking", name = "order", required = false
	)
	public int order();

	@Meta.AD(
		deflt = "http://localhost:3000/fake-api/send/sms/",
		description = "mfa-sms-url-description", name = "url", required = false
	)
	public String apiUrl();

}