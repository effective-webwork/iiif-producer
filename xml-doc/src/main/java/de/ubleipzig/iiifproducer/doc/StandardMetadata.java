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

package de.ubleipzig.iiifproducer.doc;

import de.ubleipzig.iiifproducer.model.Metadata;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static de.ubleipzig.iiifproducer.doc.MetsConstants.GOOBI_TYPE;
import static de.ubleipzig.iiifproducer.doc.MetsConstants.METS_PARENT_LOGICAL_ID;
import static de.ubleipzig.iiifproducer.doc.MetsConstants.SWB_TYPE;
import static de.ubleipzig.iiifproducer.doc.MetsConstants.URN_TYPE;
import static de.ubleipzig.iiifproducer.doc.MetsManifestBuilder.*;

/**
 * StandardMetadata.
 *
 * @author christopher-johnson
 */
@Slf4j
public class StandardMetadata {

    private final MetsData mets;

    /**
     * @param mets MetsData
     */
    public StandardMetadata(final MetsData mets) {
        this.mets = mets;
    }

    /**
     * @return List
     */
    public List<Metadata> getInfo() {
        final List<Metadata> meta = new ArrayList<>();
        meta.add(Metadata.builder().label("Kitodo").value(getManuscriptIdByType(mets, GOOBI_TYPE)).build());
        meta.add(Metadata.builder().label("URN").value(getManuscriptIdByType(mets, URN_TYPE)).build());
        //this is ugly, but this is the way that collections are tagged in the XML
        final String vd16 = getManuscriptIdByType(mets, "vd16");
        final String vd17 = getManuscriptIdByType(mets, "vd17");
        if (!vd16.equals("")) {
            meta.add(Metadata.builder().label("VD16").value(vd16).build());
            meta.add(Metadata.builder().label("Collection").value("VD16").build());
        }
        if (!vd17.equals("")) {
            meta.add(Metadata.builder().label("VD17").value(vd17).build());
            meta.add(Metadata.builder().label("Collection").value("VD17").build());
        }
        meta.add(Metadata.builder().label("Source PPN (SWB)").value(getManuscriptIdByType(mets, SWB_TYPE)).build());
        meta.add(Metadata.builder().label("Collection").value(getCollection(mets)).build());
        if (getCollection(mets).contains("TestCollection") ^ getCollection(mets).contains("Heisenberg")) {
            meta.add(Metadata.builder().label("Call number").value(getCallNumbers(mets)).build());
            meta.add(Metadata.builder().label("Date of publication").value(getDates(mets)).build());
            meta.add(Metadata.builder().label("Kalliope-ID").value(getKalliopeID(mets)).build());
        } else {
            meta.add(Metadata.builder().label("Call number").value(getCallNumber(mets)).build());
            meta.add(Metadata.builder().label("Date of publication").value(getDate(mets)).build());
        }
        meta.add(Metadata.builder().label("Owner").value(getOwner(mets)).build());
        meta.add(Metadata.builder().label("Author").value(getAuthor(mets)).build());
        meta.add(Metadata.builder().label("Addressee").value(getAddressee(mets)).build());
        meta.add(Metadata.builder().label("Place of publication").value(getPlace(mets)).build());
        meta.add(Metadata.builder().label("Publisher").value(getPublisher(mets)).build());
        meta.add(Metadata.builder().label("Physical description").value(getPhysState(mets)).build());
        meta.add(Metadata.builder().label("Manifest Type").value(getLogicalType(mets, METS_PARENT_LOGICAL_ID)).build());
        log.debug("Standard Metadata Added");
        return meta;
    }
}
