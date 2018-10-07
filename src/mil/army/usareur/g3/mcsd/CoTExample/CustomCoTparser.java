/* -----------------------------------------------------------------------------
 *       UNCLASSIFIED UNCLASSIFIED UNCLASSIFIED UNCLASSIFIED UNCLASSIFIED
 *                 (C) Copyright 2018, USAREUR G3 MCSD
 *                         ALL RIGHTS RESERVED
 *                 THIS NOTICE DOES NOT IMPLY PUBLICATION
 * -------------------------------------------------------------------------- */
package mil.army.usareur.g3.mcsd.CoTExample;

import java.io.StringReader;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import mil.af.cursorOnTarget.CotDetail;
import mil.af.cursorOnTarget.CotEvent;
import mil.af.cursorOnTarget.CotPoint;
import mil.af.cursorOnTarget.DetailSubelement;
import mil.af.cursorOnTarget.UnsupportedCotVersionException;
import mil.army.usareur.g3.mcsd.CoTUtils.CoTparser;
import mil.army.usareur.g3.mcsd.CotBindings.Track;

/**
 *
 * @author martin.c.dudel.civ@mail.mil
 */
public class CustomCoTparser extends CoTparser {

    @Override
    public void coTeventHandler(String xmlEvent) {
        //System.out.println("===== CUSTOM COT EVENT HANDLER\n-->" + xmlEvent + "<--");
        System.out.println("===== CUSTOM COT EVENT HANDLER\n" + this.parseToJSON(xmlEvent, 8));
        // Get the CoT event
        CotEvent cotEvent;

        try {
            // Trim the xml string to avoid the following error: "Content is not allowed in trailing section"
            cotEvent = CotEvent.parse(xmlEvent.trim());
            System.out.println("===== CUSTOM HANDLER COT EVENT UID: " + cotEvent.getUID());
            System.out.println("===== CUSTOM HANDLER COT EVENT TYPE: " + cotEvent.getType());
            System.out.println("===== CUSTOM HANDLER COT EVENT 2525B: " + this.convertTypeTo2525B(cotEvent.getType()));
            System.out.println("===== CUSTOM HANDLER COT EVENT DESCRIPTION: " + this.getCotTypeDescription(cotEvent.getType()));
            CotPoint point = cotEvent.getPoint();
            System.out.println("===== CUSTOM HANDLER COT POINT LAT: " + point.getLatitude());
            System.out.println("===== CUSTOM HANDLER COT POINT LON: " + point.getLongitude());
            System.out.println("===== CUSTOM HANDLER COT POINT CE: " + point.getCircularError());
            System.out.println("===== CUSTOM HANDLER COT POINT LE: " + point.getLinearError());
            System.out.println("===== CUSTOM HANDLER COT POINT HAE: " + point.getHAE());
            CotDetail detail = cotEvent.getDetail();
            Iterator dit = detail.iterator();
            while (dit.hasNext()) {
                DetailSubelement subElement = (DetailSubelement) dit.next();
                StringReader xmlReader = new StringReader(subElement.toXml());
                if ("track".equalsIgnoreCase(subElement.getName())) {
                    Track track = (Track) jaxbUnmarshallerTrack.unmarshal(xmlReader);
                    System.out.println("===== CUSTOM HANDLER COT DETAIL " + subElement.getName() + "   Course: " + track.getCourse());
                    System.out.println("===== CUSTOM HANDLER COT DETAIL " + subElement.getName() + "   ECourse: " + track.getECourse());
                    System.out.println("===== CUSTOM HANDLER COT DETAIL " + subElement.getName() + "   Slope: " + track.getSlope());
                    System.out.println("===== CUSTOM HANDLER COT DETAIL " + subElement.getName() + "   ESlope: " + track.getESlope());
                    System.out.println("===== CUSTOM HANDLER COT DETAIL " + subElement.getName() + "   Speed: " + track.getSpeed());
                    System.out.println("===== CUSTOM HANDLER COT DETAIL " + subElement.getName() + "   ESpeed: " + track.getESpeed());
                    System.out.println("===== CUSTOM HANDLER COT DETAIL " + subElement.getName() + "   Version: " + track.getVersion());
                }//track   
            }
        } catch (UnsupportedCotVersionException ex) {
            Logger.getLogger(CustomCoTparser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(CustomCoTparser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//coTeventHandler
}
