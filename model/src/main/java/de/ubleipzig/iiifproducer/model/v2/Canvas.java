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
import de.ubleipzig.iiif.vocabulary.SCEnum;
import de.ubleipzig.iiifproducer.model.Metadata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@JsonPropertyOrder({"@id", "@type", "label", "height", "width", "images"})
public class Canvas {

    @JsonProperty("@context")
    private String context;
    @JsonProperty("height")
    private Integer height;
    @JsonProperty("@id")
    private String id;
    @JsonProperty("images")
    private List<PaintingAnnotation> images;
    @JsonProperty("label")
    private String label;
    @JsonProperty("metadata")
    private List<Metadata> metadata;
    @JsonProperty
    private List<Object> otherContent;
    @JsonProperty("related")
    private Object related;
    @JsonProperty
    private SeeAlso seeAlso;
    @JsonProperty("service")
    private Object service;
    @JsonProperty("thumbnail")
    private Object thumbnail;
    @Builder.Default
    @JsonProperty("@type")
    private String type = SCEnum.Canvas.compactedIRI();
    @JsonProperty("width")
    private Integer width;
}
