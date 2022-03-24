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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.ubleipzig.iiif.vocabulary.SCEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * TemplateTopStructure.
 *
 * @author christopher-johnson
 */
@Setter
@Getter
@AllArgsConstructor
@JsonPropertyOrder({"@id", "@type", "label", "viewingHint", "ranges"})
public class TopStructure extends Structure {

    @JsonIgnore
    private List<String> canvases;
    @JsonProperty("@id")
    private String id;
    @JsonProperty("label")
    private String label;
    @JsonIgnore
    private List<Member> members;
    @JsonProperty("ranges")
    private List<String> ranges;
    @JsonProperty("@type")
    private String type;
    @JsonProperty("viewingHint")
    private String viewingHint;

    @Builder
    public TopStructure(String id, String type, String label, List<String> canvases, List<String> ranges,
                        List<Member> members) {
        super();
        this.id = id;
        this.type = SCEnum.Range.compactedIRI();
        this.label = label;
        this.canvases = canvases;
        this.ranges = ranges;
        this.members = members;
        this.viewingHint = "top";
    }

    public static class TopStructureBuilder extends StructureBuilder {
        TopStructureBuilder() {
            super();
        }
    }

}
