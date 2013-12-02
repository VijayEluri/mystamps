/*
 * Copyright (C) 2009-2014 Slava Semushin <slava.semushin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package ru.mystamps.web.tests.cases;

import static org.fest.assertions.api.Assertions.assertThat;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Value;

import ru.mystamps.web.Url;
import ru.mystamps.web.tests.page.ActivateAccountPage;

import static ru.mystamps.web.tests.TranslationUtils.stripHtmlTags;
import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.fest.AbstractPageWithFormAssert.assertThat;
import static ru.mystamps.web.validation.ValidationRules.LOGIN_MIN_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.LOGIN_MAX_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.NAME_MAX_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.PASSWORD_MIN_LENGTH;
import static ru.mystamps.web.validation.ValidationRules.ACT_KEY_LENGTH;

public class WhenAnonymousUserActivateAccount
	extends WhenAnyUserAtAnyPageWithForm<ActivateAccountPage> {
	
	@Value("${valid_user_login}")
	private String validUserLogin;
	
	@Value("${not_activated_user1_act_key}")
	private String firstNotActivatedUserActKey;
	
	@Value("${not_activated_user2_act_key}")
	private String secondNotActivatedUserActKey;
	
	public WhenAnonymousUserActivateAccount() {
		super(ActivateAccountPage.class);
		hasTitle(tr("t_activation_title"));
		hasHeader(tr("t_activation_on_site"));
	}
	
	public void openPage() {
		page.open();
	}
	
	public void shouldHaveStandardStructure() {
		checkStandardStructure();
	}
	
	public void activationKeyShouldBeAutoFilledFromURL() {
		String key = "7777744444";
		String url = Url.ACTIVATE_ACCOUNT_PAGE_WITH_KEY.replace("{key}", key);
		
		page.open(url);
		assertThat(page).field("activationKey").hasValue(key);
	}
	
	public void loginAndPasswordShouldBeDifferent() {
		page.activateAccount("admin", null, "admin", null, null);
		
		assertThat(page)
			.field("password")
			.hasError(tr("password.login.match"));
	}
	
	public void passwordAndConfirmationShouldMatch() {
		page.activateAccount(null, null, "password123", "password321", null);
		
		assertThat(page)
			.field("passwordConfirmation")
			.hasError(tr("password.mismatch"));
	}
	
	public void loginShouldNotBeTooShort() {
		page.activateAccount("a", null, null, null, null);
		
		assertThat(page)
			.field("login")
			.hasError(tr("value.too-short", LOGIN_MIN_LENGTH));
	}
	
	public void mostShortLoginShouldBeAccepted() {
		page.activateAccount("ab", null, null, null, null);
		
		assertThat(page).field("login").hasNoError();
	}
	
	public void loginShouldNotBeTooLong() {
		page.activateAccount("abcde12345fghkl6", null, null, null, null);
		
		assertThat(page)
			.field("login")
			.hasError(tr("value.too-long", LOGIN_MAX_LENGTH));
	}
	
	public void mostLongLoginShouldBeAccepted() {
		page.activateAccount("abcde1234567890", null, null, null, null);
		
		assertThat(page).field("login").hasNoError();
	}
	
	public void loginWithAllowedCharactersShouldBeAccepted() {
		page.activateAccount("t3s7-T_E_S_T", null, null, null, null);
		
		assertThat(page).field("login").hasNoError();
	}
	
	public void loginWithForbiddenCharactersShouldBeRejected() {
		page.activateAccount("'t@$t'", null, null, null, null);
		
		assertThat(page)
			.field("login")
			.hasError(tr("login.invalid"));
	}
	
	public void loginShouldBeUnique() {
		page.activateAccount(validUserLogin, null, null, null, null);
		
		assertThat(page)
			.field("login")
			.hasError(tr("ru.mystamps.web.validation.jsr303.UniqueLogin.message"));
	}
	
	public void nameShouldNotBeTooLong() {
		page.activateAccount(null, StringUtils.repeat("0", NAME_MAX_LENGTH + 1), null, null, null);
		
		assertThat(page)
			.field("name")
			.hasError(tr("value.too-long", NAME_MAX_LENGTH));
	}
	
	public void nameWithAllowedCharactersShouldBeAccepted(String name, Object whatever) {
		page.activateAccount(null, name, null, null, null);
		
		assertThat(page).field("name").hasNoError();
	}
	
	public void nameWithForbiddenCharactersShouldBeRejected() {
		page.activateAccount(null, "M@st3r_", null, null, null);
		
		assertThat(page)
			.field("name")
			.hasError(tr("name.invalid"));
	}
	
	public void nameShouldNotStartsFromHyphen() {
		page.activateAccount(null, "-test", null, null, null);
		
		assertThat(page)
			.field("name")
			.hasError(tr("name.hyphen"));
	}
	
	public void nameShouldNotEndsWithHyphen() {
		page.activateAccount(null, "test-", null, null, null);
		
		assertThat(page)
			.field("name")
			.hasError(tr("name.hyphen"));
	}
	
	public void nameShouldBeStripedFromLeadingAndTrailingSpaces() {
		page.activateAccount(null, " test ", null, null, null);
		
		assertThat(page).field("name").hasValue("test");
	}
	
	public void passwordShouldNotBeTooShort() {
		page.activateAccount(null, null, "123", null, null);
		
		assertThat(page)
			.field("password")
			.hasError(tr("value.too-short", PASSWORD_MIN_LENGTH));
	}
	
	public void mostShortPasswordShouldBeAccepted() {
		page.activateAccount(null, null, "1234", null, null);
		
		assertThat(page).field("password").hasNoError();
	}
	
	public void passwordWithAllowedCharactersShouldBeAccepted() {
		page.activateAccount(null, null, "t3s7-T_E_S_T", null, null);
		
		assertThat(page).field("password").hasNoError();
	}
	
	public void passwordWithForbiddenCharactersShouldBeRejected() {
		page.activateAccount(null, null, "'t@$t'", null, null);
		
		assertThat(page)
			.field("password")
			.hasError(tr("password.invalid"));
	}
	
	public void activationKeyShouldNotBeTooShort() {
		page.activateAccount(null, null, null, null, "12345");
		
		assertThat(page)
			.field("activationKey")
			.hasError(tr("value.invalid-length", ACT_KEY_LENGTH));
	}
	
	public void activationKeyShouldNotBeTooLong() {
		page.activateAccount(null, null, null, null, "1234567890123");
		
		assertThat(page)
			.field("activationKey")
			.hasError(tr("value.invalid-length", ACT_KEY_LENGTH));
	}
	
	public void activationKeyWithForbiddenCharactersShouldBeRejected() {
		page.activateAccount(null, null, null, null, "A123=+TEST");
		
		assertThat(page)
			.field("activationKey")
			.hasError(tr("key.invalid"));
	}
	
	public void wrongActivationKeyShouldBeRejected() {
		page.activateAccount(null, null, null, null, StringUtils.repeat("1", ACT_KEY_LENGTH));
		
		assertThat(page)
			.field("activationKey")
			.hasError(tr("ru.mystamps.web.validation.jsr303.ExistingActivationKey.message"));
	}
	
	public void afterActivationShouldExistsMessageWithLinkForAuthentication() {
		page.activateAccount(
			"1st-test-login",
			"Test Suite",
			"test-password",
			"test-password",
			firstNotActivatedUserActKey
		);
		
		assertThat(page.getCurrentUrl()).isEqualTo(Url.AUTHENTICATION_PAGE);
		
		assertThat(page.textPresent(stripHtmlTags(tr("t_activation_successful")))).isTrue();
	}
	
	public void activationShouldPassWhenUserProvidedEmptyName() {
		page.activateAccount(
			"2nd-test-login",
			"",
			"test-password",
			"test-password",
			secondNotActivatedUserActKey
		);
		
		assertThat(page.getCurrentUrl()).isEqualTo(Url.AUTHENTICATION_PAGE);
		
		assertThat(page.textPresent(stripHtmlTags(tr("t_activation_successful")))).isTrue();
	}
	
	public Object[][] getValidNames() {
		return new Object[][] {
			{"x", null},
			{"Slava Se-mushin", null},
			{"Семён Якушев", null}
		};
	}
	
}
