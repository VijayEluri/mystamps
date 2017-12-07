/**
 * Copyright (C) 2009-2017 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.service

import static io.qala.datagen.RandomElements.from
import static io.qala.datagen.RandomShortApi.nullOrBlank
import static io.qala.datagen.RandomValue.between

import static ru.mystamps.web.service.SeriesInfoExtractorServiceImpl.MAX_SUPPORTED_RELEASE_YEAR

import java.time.Year

import org.slf4j.helpers.NOPLogger

import spock.lang.Specification
import spock.lang.Unroll

import ru.mystamps.web.tests.Random
import ru.mystamps.web.validation.ValidationRules

@SuppressWarnings(['ClassJavadoc', 'MethodName', 'NoDef', 'NoTabCharacter', 'TrailingWhitespace'])
class SeriesInfoExtractorServiceImplTest extends Specification {
	
	private final CategoryService categoryService = Mock()
	private final CountryService countryService = Mock()
	private final SeriesInfoExtractorService service = new SeriesInfoExtractorServiceImpl(
		NOPLogger.NOP_LOGGER,
		categoryService,
		countryService
	)
	
	//
	// Tests for extractCategory()
	//
	
	def 'extractCategory() should return empty result when fragment is null, empty or blank'() {
		given:
			String fragment = nullOrBlank()
		when:
			List<Integer> result = service.extractCategory(fragment)
		then:
			result.isEmpty()
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'extractCategory() should try to search by category names'() {
		given:
			String fragment = 'Lorem ipsum   dolor\tsit\namet,'
			Set<String> expectedCandidates = [ 'Lorem', 'ipsum', 'dolor', 'sit', 'amet,' ]
		and:
			List<Integer> expectedResult = Random.listOfIntegers()
		when:
			List<Integer> result = service.extractCategory(fragment)
		then:
			1 * categoryService.findIdsByNames({ Set<String> candidates ->
				assert candidates == expectedCandidates
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for extractCountry()
	//
	
	def 'extractCountry() should return empty result when fragment is null, empty or blank'() {
		given:
			String fragment = nullOrBlank()
		when:
			List<Integer> result = service.extractCountry(fragment)
		then:
			result.isEmpty()
	}
	
	@SuppressWarnings(['ClosureAsLastMethodParameter', 'UnnecessaryReturnKeyword'])
	def 'extractCountry() should try to search by country names'() {
		given:
			String fragment = 'Lorem ipsum   dolor\tsit\namet,'
			Set<String> expectedCandidates = [ 'Lorem', 'ipsum', 'dolor', 'sit', 'amet,' ]
		and:
			List<Integer> expectedResult = Random.listOfIntegers()
		when:
			List<Integer> result = service.extractCountry(fragment)
		then:
			1 * countryService.findIdsByNames({ Set<String> candidates ->
				assert candidates == expectedCandidates
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for extractReleaseYear()
	//
	
	def 'extractReleaseYear() should return null when fragment is null, empty or blank'() {
		given:
			String fragment = nullOrBlank()
		when:
			Integer year = service.extractReleaseYear(fragment)
		then:
			year == null
	}
	
	def 'extractReleaseYear() should extract year from XIX century'() {
		given:
			Integer expectedYear = between(ValidationRules.MIN_RELEASE_YEAR, 1899).integer()
		and:
			String fragment = String.valueOf(expectedYear)
		when:
			Integer year = service.extractReleaseYear(fragment)
		then:
			year == expectedYear
	}
	
	def 'extractReleaseYear() should extract year from XX century'() {
		given:
			Integer expectedYear = between(1900, 1999).integer()
		and:
			String fragment = String.valueOf(expectedYear)
		when:
			Integer year = service.extractReleaseYear(fragment)
		then:
			year == expectedYear
	}
	
	def 'extractReleaseYear() should extract year from XXI century'() {
		given:
			Integer expectedYear = between(2000, MAX_SUPPORTED_RELEASE_YEAR).integer()
		and:
			String fragment = String.valueOf(expectedYear)
		when:
			Integer year = service.extractReleaseYear(fragment)
		then:
			year == expectedYear
	}
	
	@Unroll
	def 'extractReleaseYear() should extract date from "#fragment"'(String fragment) {
		given:
			Integer expectedYear = 2010 // should be in sync with examples below
		when:
			Integer year = service.extractReleaseYear(fragment)
		then:
			year == expectedYear
		where:
			fragment                         | _
			'italy 2010'                     | _
			'2010 brazil'                    | _
			'2010\t\tbrazil'                 | _
			'2010     brazil'                | _
			'prehistoric animals 2010 congo' | _
	}
	
	@SuppressWarnings('UnnecessaryGetter')
	def 'extractReleaseYear() should return the first year if there are many'() {
		given:
			Integer currentYear = Year.now().getValue()
			Integer expectedYear = between(ValidationRules.MIN_RELEASE_YEAR, currentYear).integer()
		and:
			Integer anotherYear = between(ValidationRules.MIN_RELEASE_YEAR, currentYear).integer()
		and:
			String fragment = String.format('%d %d', expectedYear, anotherYear)
		when:
			Integer year = service.extractReleaseYear(fragment)
		then:
			year == expectedYear
	}
	
	@SuppressWarnings('UnnecessaryGetter')
	def 'extractReleaseYear() should skip invalid date'() {
		given:
			Integer unsupportedYearInPast = between(0, ValidationRules.MIN_RELEASE_YEAR - 1).integer()
			Integer unsupportedYearInFuture = between(MAX_SUPPORTED_RELEASE_YEAR + 1, Integer.MAX_VALUE).integer()
			Integer unsupportedYear = from(unsupportedYearInPast, unsupportedYearInFuture).sample()
		and:
			Integer currentYear = Year.now().getValue()
			Integer expectedYear = between(ValidationRules.MIN_RELEASE_YEAR, currentYear).integer()
		and:
			String fragment = String.format('%d %d', unsupportedYear, expectedYear)
		when:
			Integer year = service.extractReleaseYear(fragment)
		then:
			year == expectedYear
	}
	
	def 'extractReleaseYear() shouldn\'t extract dates before 1840'() {
		given:
			Integer unsupportedYear = between(0, ValidationRules.MIN_RELEASE_YEAR - 1).integer()
			String fragment = String.valueOf(unsupportedYear)
		when:
			Integer year = service.extractReleaseYear(fragment)
		then:
			year == null
	}
	
	def 'extractReleaseYear() shouldn\'t extract dates after 2099'() {
		given:
			Integer unsupportedYear = between(MAX_SUPPORTED_RELEASE_YEAR + 1, Integer.MAX_VALUE).integer()
			String fragment = String.valueOf(unsupportedYear)
		when:
			Integer year = service.extractReleaseYear(fragment)
		then:
			year == null
	}
	
	@Unroll
	def 'extractReleaseYear() shouldn\'t extract date from "#fragment"'(String fragment) {
		when:
			Integer year = service.extractReleaseYear(fragment)
		then:
			year == null
		where:
			fragment           | _
			'-2000'            | _
			'test2000'         | _
			'test-2000'        | _
			'test,2000'        | _
			'test/2000'        | _
			'part of word2000' | _
	}
	
}