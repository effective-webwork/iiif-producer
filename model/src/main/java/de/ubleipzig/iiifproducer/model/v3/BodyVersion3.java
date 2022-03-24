/*
 * IIIFProducer
 * Copyright (C) 2017 Leipzig University Library <info@ub.uni-leipzig.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package de.ubleipzig.iiifproducer.model.v3;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Builder
@Setter
@Getter
@AllArgsConstructor
@JsonPropertyOrder({"id", "type", "format", "service", "height", "width"})
public class BodyVersion3 {
    @JsonProperty("format")
    private String format;
    @JsonProperty
    private Integer height;
    @JsonProperty("id")
    private String id;
    @JsonProperty
    private Map<String, List<String>> label;
    @JsonProperty
    private List<ServiceVersion3> service;
    @JsonProperty("type")
    private String type;
    @JsonProperty
    private Integer width;
}
