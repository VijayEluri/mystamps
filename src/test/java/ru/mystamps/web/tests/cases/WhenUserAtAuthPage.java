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

import static ru.mystamps.web.tests.TranslationUtils.tr;

import org.springframework.beans.factory.annotation.Value;

import ru.mystamps.web.tests.page.AuthAccountPage;

public class WhenUserAtAuthPage extends WhenAnyUserAtAnyPageWithForm<AuthAccountPage> {
	
	@Value("${valid_user_login}")
	private String validUserLogin;
	
	@Value("${valid_user_password}")
	private String validUserPassword;
	
	public WhenUserAtAuthPage() {
		super(AuthAccountPage.class);
		hasTitle(tr("t_auth_title"));
	}
	
	public void setUp() {
		page.open();
		page.login(validUserLogin, validUserPassword);
	}
	
	public void tearDown() {
		page.logout();
	}
	
	public void messageShouldBeShown() {
		assertThat(page.textPresent(tr("t_already_authenticated"))).isTrue();

	}
	
	public void formWithLegendShouldBeAbsent() {
		assertThat(page.authenticationFormExists()).isFalse();
		assertThat(page.getFormHints()).isEmpty();
	}
	
}
