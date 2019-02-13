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
package ru.mystamps.web.feature.image;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class JdbcImageDataDao implements ImageDataDao {
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	@Value("${image_data.find_by_image_id}")
	private String findByImageIdSql;
	
	@Value("${image_data.add}")
	private String addImageDataSql;
	
	@Override
	public ImageDto findByImageId(Integer imageId, boolean preview) {
		Map<String, Object> params = new HashMap<>();
		params.put("image_id", imageId);
		params.put("preview", preview);
		
		try {
			return jdbcTemplate.queryForObject(
				findByImageIdSql,
				params,
				RowMappers::forImageDto
			);
		} catch (EmptyResultDataAccessException ignored) {
			return null;
		}
	}
	
	@Override
	public Integer add(AddImageDataDbDto imageData) {
		Map<String, Object> params = new HashMap<>();
		params.put("image_id", imageData.getImageId());
		params.put("content", imageData.getContent());
		params.put("preview", imageData.isPreview());
		
		KeyHolder holder = new GeneratedKeyHolder();
		
		int affected = jdbcTemplate.update(
			addImageDataSql,
			new MapSqlParameterSource(params),
			holder
		);
		
		Validate.validState(
			affected == 1,
			"Unexpected number of affected rows after creation of image's data: %d",
			affected
		);
		
		return Integer.valueOf(holder.getKey().intValue());
	}
	
}
