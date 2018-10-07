/*
 * The MIT License
 *
 * Copyright 2018 Martin Dudel.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package mil.army.usareur.g3.mcsd.CoTUtils;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import mil.af.cursorOnTarget.CotDetail;
import mil.af.cursorOnTarget.CotEvent;
import mil.af.cursorOnTarget.CotPoint;
import mil.af.cursorOnTarget.DetailSubelement;
import mil.af.cursorOnTarget.Remarks;
import mil.af.cursorOnTarget.UnsupportedCotVersionException;
import mil.army.usareur.g3.mcsd.CotBindings.Contact;
import mil.army.usareur.g3.mcsd.CotBindings.FlowTags;
import mil.army.usareur.g3.mcsd.CotBindings.Image;
import mil.army.usareur.g3.mcsd.CotBindings.Link;
import mil.army.usareur.g3.mcsd.CotBindings.Sensor;
import mil.army.usareur.g3.mcsd.CotBindings.Shape;
import mil.army.usareur.g3.mcsd.CotBindings.Spatial;
import mil.army.usareur.g3.mcsd.CotBindings.Track;
import mil.army.usareur.g3.mcsd.CotBindings.Uid;
import org.json.JSONObject;
import org.json.XML;

/**
 *
 * @author martin.c.dudel.civ@mail.mil
 */
public class CoTparser {

    public JAXBContext jaxbContextContact;
    public Unmarshaller jaxbUnmarshallerContact;
    public JAXBContext jaxbContextFlowTags;
    public Unmarshaller jaxbUnmarshallerFlowTags;
    public JAXBContext jaxbContextImage;
    public Unmarshaller jaxbUnmarshallerImage;
    public JAXBContext jaxbContextLink;
    public Unmarshaller jaxbUnmarshallerLink;
    public JAXBContext jaxbContextRemarks;
    public Unmarshaller jaxbUnmarshallerRemarks;
    public JAXBContext jaxbContextSensor;
    public Unmarshaller jaxbUnmarshallerSensor;
    public JAXBContext jaxbContextShape;
    public Unmarshaller jaxbUnmarshallerShape;
    public JAXBContext jaxbContextSpatial;
    public Unmarshaller jaxbUnmarshallerSpatial;
    public JAXBContext jaxbContextTrack;
    public Unmarshaller jaxbUnmarshallerTrack;
    public JAXBContext jaxbContextUid;
    public Unmarshaller jaxbUnmarshallerUid;
    public CoTtypes cotTypes = new CoTtypes();

    public CoTparser() {
        // Create all the marshallers once at class construction
        try {
            jaxbContextContact = JAXBContext.newInstance(Contact.class);
            jaxbUnmarshallerContact = jaxbContextContact.createUnmarshaller();
            jaxbContextFlowTags = JAXBContext.newInstance(FlowTags.class);
            jaxbUnmarshallerFlowTags = jaxbContextFlowTags.createUnmarshaller();
            jaxbContextImage = JAXBContext.newInstance(Image.class);
            jaxbUnmarshallerImage = jaxbContextImage.createUnmarshaller();
            jaxbContextLink = JAXBContext.newInstance(Link.class);
            jaxbUnmarshallerLink = jaxbContextLink.createUnmarshaller();
            jaxbContextRemarks = JAXBContext.newInstance(Remarks.class);
            jaxbUnmarshallerRemarks = jaxbContextRemarks.createUnmarshaller();
            jaxbContextSensor = JAXBContext.newInstance(Sensor.class);
            jaxbUnmarshallerSensor = jaxbContextSensor.createUnmarshaller();
            jaxbContextShape = JAXBContext.newInstance(Shape.class);
            jaxbUnmarshallerShape = jaxbContextShape.createUnmarshaller();
            jaxbContextSpatial = JAXBContext.newInstance(Spatial.class);
            jaxbUnmarshallerSpatial = jaxbContextSpatial.createUnmarshaller();
            jaxbContextTrack = JAXBContext.newInstance(Track.class);
            jaxbUnmarshallerTrack = jaxbContextTrack.createUnmarshaller();
            jaxbContextUid = JAXBContext.newInstance(Uid.class);
            jaxbUnmarshallerUid = jaxbContextUid.createUnmarshaller();
        } catch (JAXBException ex) {
            Logger.getLogger(CoTparser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//CoTparser()

    /**
     * Convert a Cursor on Target XML object to JSON.
     *
     * @param xmlEvent String of CoT XML
     *
     * @return a string JSON representation of the CoT object
     */
    public String parseToJSON(String xmlEvent) {
        return this.parseToJSON(xmlEvent, 3);
    }//parseToJSON(String xmlEvent)

    /**
     * Convert a Cursor on Target XML object to JSON.
     *
     * @param xmlEvent String of CoT XML
     * @param indentFactor int determining how many spaces to indent the JSON
     * string
     * @return a string JSON representation of the CoT object
     */
    public String parseToJSON(String xmlEvent, int indentFactor) {
        if (indentFactor < 0) {
            indentFactor = 3;
        }
        JSONObject xmlJSONObj = XML.toJSONObject(xmlEvent);
        String type = xmlJSONObj.getJSONObject("event").getString("type");
        // check for event -> detail object
        if (xmlJSONObj.getJSONObject("event").has("detail")) {
            xmlJSONObj.getJSONObject("event").getJSONObject("detail").put("symbolCode", convertTypeTo2525B(type));
            xmlJSONObj.getJSONObject("event").put("description", getCotTypeDescription(type));
        } else {
            JSONObject symbolObject = new JSONObject();
            symbolObject.put("symbolcode", convertTypeTo2525B(type));
            symbolObject.put("description", getCotTypeDescription(type));
            xmlJSONObj.getJSONObject("event").put("detail", symbolObject);
        }
        String jsonCoT = xmlJSONObj.toString(indentFactor);
        return jsonCoT;
    }//parseToJSON(String xmlEvent, int indentFactor)

    /**
     * Convert a CoT type to a 2525B symbol code
     *
     * @param type The CoT type representing the symbol
     * @return a String representing a 2525B symbol code
     */
    public String convertTypeTo2525B(String type) {
        String reducedType = type.replaceAll("-", "");
        char[] charArray = reducedType.toCharArray();
        //String[] typeArray = type.split("-");
        if (charArray[0] == 'a') {
            StringBuilder symbolCode = new StringBuilder("S--P-----------");
            symbolCode.setCharAt(1, charArray[1]);
            symbolCode.setCharAt(2, charArray[2]);
            int modifierLength = reducedType.length() - 2;
            if ((modifierLength > 0) && (modifierLength < 12)) {
                symbolCode.replace(4, 4 + modifierLength, reducedType.substring(3));
            }
            return symbolCode.toString().toUpperCase();
        }// atom
        if (type.startsWith("b-g")) {
            StringBuilder symbolCode = new StringBuilder("G--P------****X");
            if (reducedType.length() > 3) {
                symbolCode.setCharAt(1, charArray[2]);
                symbolCode.setCharAt(2, charArray[3]);
                if ((reducedType.length() > 4) && (reducedType.length() < 16)) {
                    symbolCode.replace(4, reducedType.length(), reducedType.substring(4));
                }
            }
            return symbolCode.toString().toUpperCase();
        }// tactical graphics
        if (type.startsWith("b-r")) {
            StringBuilder symbolCode = new StringBuilder("*--P--------***");
            if (reducedType.length() > 3) {
                symbolCode.setCharAt(2, charArray[4]);
                symbolCode.setCharAt(1, charArray[2]);
                symbolCode.setCharAt(0, charArray[3]);
                if ((reducedType.length() > 4) && (reducedType.length() < 16)) {
                    symbolCode.replace(4, reducedType.length() - 1, reducedType.substring(5));
                }
            }
            return symbolCode.toString().toUpperCase();
        }//intelligence or mootw
        if (charArray[0] == 'r') {
            return "EUIPD-----*****";
        }//cbrn
//        if (type.startsWith("b-w")) {
//            // WA-DPFW----L---
//            //                012345
//            // b-w-A-P-F-W => bwAPFW
//            StringBuilder symbolCode = new StringBuilder("W--D--------***");
//            if (reducedType.length() > 3) {
//                symbolCode.setCharAt(1, charArray[2]);
//                if ((reducedType.length() > 4) && (reducedType.length() < 16)) {
//                    symbolCode.replace(4, reducedType.length() - 3, reducedType.substring(4));
//                }
//            }
//            return symbolCode.toString().toUpperCase();
//        }//metoc      
        return "SUZA-----------"; // unknown 
    }//convertTypeTo2525B

    /**
     * Get the description for a CoT type.
     *
     * @param type The CoT type
     * @return a String of the type's human readable description
     */
    public String getCotTypeDescription(String type) {
        StringBuilder t = new StringBuilder(type);
        // a-.- Atom
        // b-g-. Tactical graphics
        // b-r-. Intelligence
        // b-x Non-CoT object
        // b-w Weather
        // b Bits
        // c Capability
        // t Tasking
        // y Reply
        // r CBRN
        if (type.charAt(0) == 'a') {
            // a atom
            t.setCharAt(2, '.');
        } else if (type.startsWith("b-g") || type.startsWith("b-r")) {
            t.setCharAt(4, '.');
        }
        return cotTypes.getDescription(t.toString());
    }//getCotTypeDescription

    /**
     * A stub CoT event handler that should be overriden with a developer
     * specific implementation of a CoT event handler
     *
     * @param xmlEvent String of CoT XML
     */
    public void coTeventHandler(String xmlEvent) {
        System.out.println("Override this method to create a custom CoT xml parser event");
        dumpCoTevent(xmlEvent);
    }

    /**
     * A debug method that dumps the CoT object to the console
     *
     * @param xmlEvent String of CoT XML
     */
    public void dumpCoTevent(String xmlEvent) {
        // Dump the CoT XML to the console and parse the objects. This class is
        // primarily a debug feature.
        try {
            xmlEvent = xmlEvent.substring(0, xmlEvent.lastIndexOf(">") + 1);
            System.out.println("========== Begin xml:\n" + xmlEvent + "\n========== End xml");

            CotEvent cotEvent = CotEvent.parse(xmlEvent);
            System.out.println("Access: " + cotEvent.getAccess());
            System.out.println("How: " + cotEvent.getHow());
            System.out.println("Opex: " + cotEvent.getOpex());
            System.out.println("Type: " + cotEvent.getType());
            System.out.println("Description: " + getCotTypeDescription(cotEvent.getType()));
            System.out.println("Calculated Symbol Code: " + convertTypeTo2525B(cotEvent.getType()));
            System.out.println("UID: " + cotEvent.getUID());
            System.out.println("Stale: " + cotEvent.getStale());
            System.out.println("Start: " + cotEvent.getStart());
            System.out.println("Time: " + cotEvent.getTime());
            System.out.println("Version: " + cotEvent.getVersion());
            System.out.println("Quality of service: " + cotEvent.getQos());

            CotPoint point = cotEvent.getPoint();
            System.out.println("Point    CircularError: " + point.getCircularError());
            System.out.println("Point    HAE: " + point.getHAE());
            System.out.println("Point    Latitude: " + point.getLatitude());
            System.out.println("Point    Longitude: " + point.getLongitude());
            System.out.println("Point    LinearError: " + point.getLinearError());

            CotDetail detail = cotEvent.getDetail();
            System.out.println("Detail count: " + detail.size());
            Iterator dit = detail.iterator();
            while (dit.hasNext()) {
                DetailSubelement subElement = (DetailSubelement) dit.next();
                System.out.println("Detail    sub-element name: " + subElement.getName());
                System.out.println("Detail    sub-element xml: " + subElement.toXml());
                StringReader xmlReader = new StringReader(subElement.toXml());
                if ("contact".equalsIgnoreCase(subElement.getName())) {
                    Contact contact = (Contact) jaxbUnmarshallerContact.unmarshal(xmlReader);
                    System.out.println("Detail       " + subElement.getName() + "   Callsign: " + contact.getCallsign());
                    System.out.println("Detail       " + subElement.getName() + "   Dsn: " + contact.getDsn());
                    System.out.println("Detail       " + subElement.getName() + "   Email: " + contact.getEmail());
                    System.out.println("Detail       " + subElement.getName() + "   Hostname: " + contact.getHostname());
                    System.out.println("Detail       " + subElement.getName() + "   Modulation: " + contact.getModulation());
                    System.out.println("Detail       " + subElement.getName() + "   Phone: " + contact.getPhone());
                }//contact
                if ("uid".equalsIgnoreCase(subElement.getName())) {
                    Uid uid = (Uid) jaxbUnmarshallerUid.unmarshal(xmlReader);
                    System.out.println("Detail       " + subElement.getName() + "   Version: " + uid.getVersion());
                    Map<QName, String> uidMap = uid.getOtherAttributes();
                    for (QName key : uidMap.keySet()) {
                        System.out.println("Detail       " + subElement.getName() + "   " + key.toString() + ": " + uidMap.get(key));
                    }
                }//uid    
                if ("track".equalsIgnoreCase(subElement.getName())) {
                    Track track = (Track) jaxbUnmarshallerTrack.unmarshal(xmlReader);
                    System.out.println("Detail       " + subElement.getName() + "   Course: " + track.getCourse());
                    System.out.println("Detail       " + subElement.getName() + "   ECourse: " + track.getECourse());
                    System.out.println("Detail       " + subElement.getName() + "   Slope: " + track.getSlope());
                    System.out.println("Detail       " + subElement.getName() + "   ESlope: " + track.getESlope());
                    System.out.println("Detail       " + subElement.getName() + "   Speed: " + track.getSpeed());
                    System.out.println("Detail       " + subElement.getName() + "   ESpeed: " + track.getESpeed());
                    System.out.println("Detail       " + subElement.getName() + "   Version: " + track.getVersion());
                }//track   
                if ("spatial".equalsIgnoreCase(subElement.getName())) {
                    Spatial spatial = (Spatial) jaxbUnmarshallerSpatial.unmarshal(xmlReader);
                    System.out.println("Detail       " + subElement.getName() + "   Spin: " + spatial.getSpin());
                    System.out.println("Detail       " + subElement.getName() + "   Attitude: " + spatial.getAttitude());
                    System.out.println("Detail       " + subElement.getName() + "   Version: " + spatial.getVersion());
                }//spatial  
                if ("shape".equalsIgnoreCase(subElement.getName())) {
                    Shape shape = (Shape) jaxbUnmarshallerShape.unmarshal(xmlReader);
                    System.out.println("Detail       " + subElement.getName() + "   Dxf: " + shape.getDxf());
                    System.out.println("Detail       " + subElement.getName() + "   Ellipse: " + shape.getEllipse());
                    System.out.println("Detail       " + subElement.getName() + "   Polyline: " + shape.getPolyline());
                    System.out.println("Detail       " + subElement.getName() + "   Version: " + shape.getVersion());
                }//shape    
                if ("sensor".equalsIgnoreCase(subElement.getName())) {
                    Sensor sensor = (Sensor) jaxbUnmarshallerSensor.unmarshal(xmlReader);
                    System.out.println("Detail       " + subElement.getName() + "   Model: " + sensor.getModel());
                    System.out.println("Detail       " + subElement.getName() + "   Ellipse: " + sensor.getType());
                    System.out.println("Detail       " + subElement.getName() + "   Azimuth: " + sensor.getAzimuth());
                    System.out.println("Detail       " + subElement.getName() + "   Elevation: " + sensor.getElevation());
                    System.out.println("Detail       " + subElement.getName() + "   Fov: " + sensor.getFov());
                    System.out.println("Detail       " + subElement.getName() + "   North: " + sensor.getNorth());
                    System.out.println("Detail       " + subElement.getName() + "   Range: " + sensor.getRange());
                    System.out.println("Detail       " + subElement.getName() + "   Roll: " + sensor.getRoll());
                    System.out.println("Detail       " + subElement.getName() + "   Vfov: " + sensor.getVfov());
                    System.out.println("Detail       " + subElement.getName() + "   Version: " + sensor.getVersion());
                    List<Object> any = sensor.getAny();
                    // Todo: figure out what is in any
                }//sensor      
                if ("remarks".equalsIgnoreCase(subElement.getName())) {
                    Remarks remarks = (Remarks) jaxbUnmarshallerRemarks.unmarshal(xmlReader);
                    System.out.println("Detail       " + subElement.getName() + "   Contents: " + remarks.getContents());
                    System.out.println("Detail       " + subElement.getName() + "   Keywords: " + remarks.getKeywords());
                    System.out.println("Detail       " + subElement.getName() + "   Name: " + remarks.getName());
                    System.out.println("Detail       " + subElement.getName() + "   Source: " + remarks.getSource());
                    System.out.println("Detail       " + subElement.getName() + "   To: " + remarks.getTo());
                    System.out.println("Detail       " + subElement.getName() + "   Time: " + remarks.getTime());
                    System.out.println("Detail       " + subElement.getName() + "   Version: " + remarks.getVersion());
                }//remarks    
                if ("link".equalsIgnoreCase(subElement.getName())) {
                    Link link = (Link) jaxbUnmarshallerLink.unmarshal(xmlReader);
                    System.out.println("Detail       " + subElement.getName() + "   Mime: " + link.getMime());
                    System.out.println("Detail       " + subElement.getName() + "   Relation: " + link.getRelation());
                    System.out.println("Detail       " + subElement.getName() + "   Remarks: " + link.getRemarks());
                    System.out.println("Detail       " + subElement.getName() + "   Type: " + link.getType());
                    System.out.println("Detail       " + subElement.getName() + "   Uid: " + link.getUid());
                    System.out.println("Detail       " + subElement.getName() + "   Url: " + link.getUrl());
                    System.out.println("Detail       " + subElement.getName() + "   Version: " + link.getVersion());
                    List<Object> any = link.getAny();
                    // Todo: figure out what is in any                    
                }//link   
                if ("image".equalsIgnoreCase(subElement.getName())) {
                    Image image = (Image) jaxbUnmarshallerImage.unmarshal(xmlReader);
                    System.out.println("Detail       " + subElement.getName() + "   Mime: " + image.getMime());
                    System.out.println("Detail       " + subElement.getName() + "   Mimecsv: " + image.getMimecsv());
                    System.out.println("Detail       " + subElement.getName() + "   Quality: " + image.getQuality());
                    System.out.println("Detail       " + subElement.getName() + "   Reason: " + image.getReason());
                    System.out.println("Detail       " + subElement.getName() + "   Source: " + image.getSource());
                    System.out.println("Detail       " + subElement.getName() + "   Type: " + image.getType());
                    System.out.println("Detail       " + subElement.getName() + "   Url: " + image.getUrl());
                    System.out.println("Detail       " + subElement.getName() + "   Bands: " + image.getBands());
                    System.out.println("Detail       " + subElement.getName() + "   Fov: " + image.getFov());
                    System.out.println("Detail       " + subElement.getName() + "   Height: " + image.getHeight());
                    System.out.println("Detail       " + subElement.getName() + "   North: " + image.getNorth());
                    System.out.println("Detail       " + subElement.getName() + "   Resolution: " + image.getResolution());
                    System.out.println("Detail       " + subElement.getName() + "   Size: " + image.getSize());
                    System.out.println("Detail       " + subElement.getName() + "   Version: " + image.getVersion());
                    System.out.println("Detail       " + subElement.getName() + "   Width: " + image.getWidth());
                    List<Object> content = image.getContent();
                    // Todo: figure out what is in content                    
                }//image   
                if (subElement.getName().toLowerCase().contains("flow")) {
                    FlowTags flowTags = (FlowTags) jaxbUnmarshallerFlowTags.unmarshal(xmlReader);
                    System.out.println("Detail       " + subElement.getName() + "   Mime: " + flowTags.getVersion());
                    List<Object> content = flowTags.getAny();
                    // Todo: figure out what is in content  
                    Map<QName, String> flowTagMap = flowTags.getOtherAttributes();
                    for (QName key : flowTagMap.keySet()) {
                        System.out.println("Detail       " + subElement.getName() + "   " + key.toString() + ": " + flowTagMap.get(key));
                    }
                }//flowTags                 
            }//while details

        } catch (UnsupportedCotVersionException ex) {
            Logger.getLogger(CoTparser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CoTparser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//dumpCoTevent
}//class CoTparser
