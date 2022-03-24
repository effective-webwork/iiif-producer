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

package de.ubleipzig.iiifproducer.converter;

import de.ubleipzig.iiifproducer.model.Metadata;
import de.ubleipzig.iiifproducer.model.v2.Structure;
import de.ubleipzig.iiifproducer.model.v3.Item;
import de.ubleipzig.iiifproducer.model.v3.MetadataVersion3;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.MultiValuedMap;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.ubleipzig.iiifproducer.converter.ConverterUtils.buildLabelMap;
import static de.ubleipzig.iiifproducer.converter.ConverterUtils.buildPaddedCanvases;
import static de.ubleipzig.iiifproducer.converter.DomainConstants.DEUTSCH;
import static de.ubleipzig.iiifproducer.converter.DomainConstants.ENGLISH;
import static de.ubleipzig.iiifproducer.converter.DomainConstants.PERIOD;
import static de.ubleipzig.iiifproducer.converter.DomainConstants.baseUrl;
import static de.ubleipzig.iiifproducer.converter.DomainConstants.structureBase;
import static de.ubleipzig.iiifproducer.converter.MetadataApiEnum.DISPLAYORDER;
import static de.ubleipzig.iiifproducer.converter.MetadataImplVersion3.deutschLabels;
import static de.ubleipzig.iiifproducer.converter.MetadataImplVersion3.englishLabels;
import static de.ubleipzig.iiifproducer.model.v3.TypeConstants.CANVAS;
import static de.ubleipzig.iiifproducer.model.v3.TypeConstants.RANGE;
import static java.io.File.separator;
import static java.util.Optional.ofNullable;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class StructureBuilderVersion3 {
    private final List<Structure> structures;
    private final String viewId;
    private final Map<String, String> backReferenceMap = new HashMap<>();
    MetadataImplVersion3 metadataImplVersion3;
    Set<String> enFilteredLabels;
    Set<String> deFilteredLabels;

    public void buildIncrements() {
        final AtomicInteger ai = new AtomicInteger(0);
        structures.forEach(s -> {
            final Optional<List<String>> cs = ofNullable(s.getCanvases());

            if (cs.isPresent()) {
                final List<String> paddedCanvases = buildPaddedCanvases(cs.get(), viewId);
                if (!paddedCanvases.isEmpty()) {
                    s.setCanvases(paddedCanvases);
                }
            }

            final String structureId = s.getId();
            if (!structureId.contains("LOG") || !structureId.contains("r0")) {
                if (ai.get() == 0) {
                    final String newStructureId = baseUrl + viewId + separator + structureBase + separator + "LOG_0000";
                    backReferenceMap.put(s.getId(), newStructureId);
                    //unset within (fix for early manifests)
                    s.setWithin(null);
                    ai.getAndIncrement();
                } else {
                    backReferenceMap.put(s.getId(), s.getId());
                    //unset within (fix for early manifests)
                    s.setWithin(null);
                }
            }
        });
    }

    public List<Item> build() {
        metadataImplVersion3 = MetadataImplVersion3.builder().build();
        final List<Item> newStructures = new ArrayList<>();
        for (Structure struct : structures) {
            final Item newStructure = Item.builder().build();
            final Optional<List<String>> ranges = ofNullable(struct.getRanges());
            final List<Item> newRanges = new ArrayList<>();
            final List<Item> newCanvases = new ArrayList<>();
            final Optional<List<String>> canvases = ofNullable(struct.getCanvases());
            canvases.ifPresent(cs -> cs.forEach(c -> {
                final Item newCanvas = Item.builder()
                        .id(c)
                        .type(CANVAS)
                        .build();
                newCanvases.add(newCanvas);
            }));

            if (ranges.isPresent()) {
                for (String r1 : ranges.get()) {
                        String sId = null;
                        try {
                            sId = new URL(r1).getPath().split(separator)[3];
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        final Item nr = Item.builder()
                                .id(r1)
                                .type(RANGE)
                                .build();
                        final Optional<List<String>> structureLabels = ofNullable(
                                buildStructureLabelsForId(sId));
                        if (structureLabels.isPresent()) {
                            List<String> rangeLabels = structureLabels.get();
                            if (!rangeLabels.isEmpty()) {
                                final Map<String, List<String>> rangeLabelMap = new HashMap<>();
                                rangeLabelMap.put(DEUTSCH, rangeLabels);
                                nr.setLabel(rangeLabelMap);
                            }
                        }
                        final Optional<List<MetadataVersion3>> structureMetadata = ofNullable(
                                buildStructureMetadataForId(sId));
                        if (structureMetadata.isPresent()) {
                            List<MetadataVersion3> m = structureMetadata.get();
                            if (!m.isEmpty()) {
                                nr.setMetadata(m);
                            }
                        }
                        newRanges.add(nr);
                }
            }
            final Stream<Item> combinedItems = Stream.concat(newRanges.stream(), newCanvases.stream());
            final List<Item> newItems = combinedItems.collect(Collectors.toList());
            newStructure.setItems(newItems);
            final String structId = struct.getId();
            newStructure.setId(structId);
            final String sId;
            try {
                sId = new URL(structId).getPath().split(separator)[3];
               final Optional<List<MetadataVersion3>> structureMetadata = ofNullable(
                        buildStructureMetadataForId(sId));
                if (structureMetadata.isPresent()) {
                    List<MetadataVersion3> m = structureMetadata.get();
                    if (!m.isEmpty()) {
                        structureMetadata.ifPresent(newStructure::setMetadata);
                    }
                }

                final Optional<List<String>> structureLabels = ofNullable(
                        buildStructureLabelsForId(sId));
                if (structureLabels.isPresent()) {
                    final Map<String, List<String>> labelMap = new HashMap<>();
                    List<String> m = structureLabels.get();
                    if (!m.isEmpty()) {
                        labelMap.put(DEUTSCH, m);
                        newStructure.setLabel(labelMap);
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            newStructures.add(newStructure);
        }
        //finally merge all substructures into top structure
        final Optional<Item> topStructure = newStructures.stream().filter(
                s -> s.getId().contains("LOG_0000")).findAny();
        if (topStructure.isPresent()) {
            final Map<String, List<String>> topStructurelabelMap = buildLabelMap("Contents", ENGLISH);
            topStructurelabelMap.put(DEUTSCH, List.of("Inhalt"));
            topStructure.get().setLabel(topStructurelabelMap);
            final List<Item> topStructureItems = topStructure.get().getItems();
            topStructureItems.forEach(ti -> {
                final Optional<Item> item = newStructures.stream()
                        .filter(s -> s.getId().contains(ti.getId())).findAny();
                if (item.isPresent()) {
                    final Map<String, List<String>> label = item.get().getLabel();
                    ti.setLabel(label);
                    Optional<List<MetadataVersion3>> meta = ofNullable(ti.getMetadata());
                    if (meta.isPresent()) {
                        List<MetadataVersion3> m = meta.get();
                        if (!m.isEmpty()) {
                            meta.ifPresent(ti::setMetadata);
                        }
                    }
                    final List<Item> items = item.get().getItems();
                    final List<Item> ranges = items.stream().filter(t -> t.getType().equals("Range")).collect(
                            Collectors.toList());
                    if (ranges.isEmpty()) {
                        ti.setItems(items);
                    } else {
                        final List<Item> subItems = new ArrayList<>();
                        ranges.forEach(si -> {
                            final Item subItem = Item.builder().build();
                            final String siId = si.getId();
                            final Map<String, List<String>> labelMap = si.getLabel();
                            subItem.setLabel(labelMap);
                            subItem.setType(RANGE);
                            subItem.setId(siId);
                            Optional<List<MetadataVersion3>> meta2 = ofNullable(si.getMetadata());
                            meta2.ifPresent(subItem::setMetadata);
                            final List<Item> subRange = newStructures.stream().filter(
                                    i -> i.getId().equals(siId)).collect(Collectors.toList());
                            final List<Item> subRangeCanvases = subRange.get(0).getItems();
                            subItem.setItems(subRangeCanvases);
                            subItems.add(subItem);
                        });
                        ti.setItems(subItems);
                    }
                }
            });
            final List<Item> finalStructure = new ArrayList<>();
            final Item top = topStructure.get();
            top.setType(RANGE);
            finalStructure.add(top);
            return finalStructure;
        }
        return null;
    }

    public List<MetadataVersion3> buildStructureMetadataForId(final String structId) {
        final Optional<List<Structure>> filteredSubList = Optional.of(
                structures.stream().filter(s -> s.getId().contains(structId)).collect(Collectors.toList()));
        List<MetadataVersion3> finalMetadata = new ArrayList<>();
        filteredSubList.ifPresent(maps -> maps.forEach(sm -> {
            final Optional<List<Metadata>> metadata = ofNullable(sm.getMetadata());
            if (metadata.isPresent()) {
                MultiValuedMap<String, String> newMetadata = metadataImplVersion3.convertListToMap(sm.getMetadata());
                List<MetadataVersion3> em = buildFilteredLabelMetadata(newMetadata, englishLabels);
                List<MetadataVersion3> dm = buildFilteredLabelMetadata(newMetadata, deutschLabels);
                finalMetadata.addAll(em);
                finalMetadata.addAll(dm);
            }
        }));
        return finalMetadata;
    }

    public List<String> buildStructureLabelsForId(final String structId) {
        final Optional<List<Structure>> filteredSubList = Optional.of(
                structures.stream().filter(s -> s.getId().contains(structId)).collect(Collectors.toList()));
        List<String> labels = new ArrayList<>();
        filteredSubList.ifPresent(maps -> maps.forEach(sm -> {
            Optional<String> labelOpt = ofNullable((String) sm.getLabel());
            labelOpt.ifPresent(labels::add);
        }));
        return labels;
    }

    List<MetadataVersion3> buildFilteredLabelMetadata(final MultiValuedMap<String, String> newMetadata,
                                  final ResourceBundle bundle) {
        final List<MetadataVersion3> metadata = new ArrayList<>();
        final Set<String> enFilteredLabelKeys = metadataImplVersion3.buildFilteredLabelSet(englishLabels);
        final Set<String> deFilteredLabelKeys = metadataImplVersion3.buildFilteredLabelSet(deutschLabels);
        if (DEUTSCH.equals(bundle.getLocale().toLanguageTag())) {
            for (String labelKey : deFilteredLabelKeys) {
                final Map<String, List<String>> labelMap = new HashMap<>();
                final String displayLabel = bundle.getString(labelKey);
                final String displayOrderKey = labelKey + PERIOD + DISPLAYORDER.getApiKey();
                final Integer displayOrder = Integer.valueOf(bundle.getString(displayOrderKey));
                Optional<Set<String>> enlabelSet =
                        Optional.of(enFilteredLabelKeys.stream().filter(en -> en.contains(labelKey)).collect(Collectors.toSet()));
                final String enLabel = enlabelSet.get().stream().findFirst().orElse(null);
                if (enLabel != null) {
                    final Optional<String> englishDisplayLabel = Optional.of(englishLabels.getString(enLabel));
                    englishDisplayLabel.ifPresent(s -> labelMap.put(ENGLISH, Collections.singletonList(s)));
                }
                labelMap.put(DEUTSCH, Collections.singletonList(displayLabel));
                List<String> values = new ArrayList<>(newMetadata.get(displayLabel));
                final MetadataVersion3 m = metadataImplVersion3.buildMetadata(labelMap, values, displayOrder);
                if (m != null && !m.getValue().isEmpty()) {
                    metadata.add(m);
                }
            };
        } else {
            for (String labelKey : enFilteredLabelKeys) {
                final Map<String, List<String>> labelMap = new HashMap<>();
                final String displayLabel = bundle.getString(labelKey);
                final String displayOrderKey = labelKey + PERIOD + DISPLAYORDER.getApiKey();
                final Integer displayOrder = Integer.valueOf(bundle.getString(displayOrderKey));
                Optional<Set<String>> delabelSet =
                        Optional.of(deFilteredLabelKeys.stream().filter(en -> en.contains(labelKey)).collect(Collectors.toSet()));
                final String deLabel = delabelSet.get().stream().findFirst().orElse(null);
                if (deLabel != null) {
                    final Optional<String> deutschDisplayLabel = Optional.of(
                            deutschLabels.getString(deLabel));
                    deutschDisplayLabel.ifPresent(s -> labelMap.put(DEUTSCH, Collections.singletonList(s)));
                }
                labelMap.put(ENGLISH, Collections.singletonList(displayLabel));
                List<String> values = new ArrayList<>(newMetadata.get(displayLabel));
                final MetadataVersion3 m = metadataImplVersion3.buildMetadata(labelMap, values, displayOrder);
                if (m != null && !m.getValue().isEmpty()) {
                    metadata.add(m);
                }
            };
        }
        return metadata;
    }
}
