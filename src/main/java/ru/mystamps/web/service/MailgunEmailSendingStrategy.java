/*
 * Copyright (C) 2009-2019 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.service;

/**
 * Sending e-mails with Mailgun service.
 */
// Links in javadoc are long
@SuppressWarnings("checkstyle:linelength")
public interface MailgunEmailSendingStrategy {

	/**
	 * Send an e-mail.
	 *
	 * @param toEmail recipient address
	 * @param fromEmail sender address
	 * @param fromName sender name
	 * @param subject mail subject
	 * @param text mail body
	 * @param tag a tag for messages categorization in Mailgun
	 * @param testMode tells Mailgun to accept a message but don't send it
	 * @throws ru.mystamps.web.service.exception.EmailSendingException when any error occurs
	 * @see SmtpMailgunEmailSendingStrategy
	 * @see <a href="https://documentation.mailgun.com/en/latest/user_manual.html#tagging">Tagging</a>
	 * @see <a href="https://documentation.mailgun.com/en/latest/user_manual.html#sending-in-test-mode">Sending in Test Mode</a>
	 */
	@SuppressWarnings("PMD.UseObjectForClearerAPI")
	void send(
		String toEmail,
		String fromEmail,
		String fromName,
		String subject,
		String text,
		String tag,
		boolean testMode);
}
