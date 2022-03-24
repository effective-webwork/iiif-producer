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

package de.ubleipzig.iiifproducer.model.v2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * TemplateBody.
 *
 * @author christopher-johnson
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@JsonPropertyOrder({"id", "type", "label", "format", "height", "width", "service"})
public class Body {

    @Builder.Default
    @JsonProperty
    private String format = "image/jpeg";
    @JsonProperty
    private Integer height;
    @JsonProperty("@id")
    private String id;
    @JsonProperty
    private String label;
    @JsonProperty
    private Service service;
    @Builder.Default
    @JsonProperty("@type")
    private String type = "dctypes:Image";
    @JsonProperty
    private Integer width;
}
