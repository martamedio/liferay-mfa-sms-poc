/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

import {fetch} from 'frontend-js-web';

export default function propsTransformer({
	additionalProps: {url},
	...otherProps
}) {
	return {
		...otherProps,
		onClick() {
			fetch(url, {method: 'POST'})
				.then((response) => {
					if (response.status == 200) {
						this.document.activeElement.disabled = true;
					}
				})
				.catch((error) => {
					alert('There was an error:' + error);
				});
		},
	};
}
