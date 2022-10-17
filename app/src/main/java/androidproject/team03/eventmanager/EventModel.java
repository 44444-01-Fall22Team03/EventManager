package androidproject.team03.eventmanager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

public class EventModel {
    public static class Events {
        public String eventID;
        public String eventName;
        public String eventStartDt;
        public String eventEndDt;
        public String eventLocation;
        public String eventDescription;
        public String eventRepeatEndDt;
        public String eventRecurrence;
        public String eventRepeatEvery;
        public boolean isSun, isMon, isTues, isWed, isThurs, isFri, isSat;

        public Events(String eventID, String eventName, String eventStartDt, String eventEndDt, String eventLocation, String eventDescription, String eventRepeatEndDt, String eventRecurrence, String eventRepeatEvery, boolean isSun, boolean isMon, boolean isTues, boolean isWed, boolean isThurs, boolean isFri, boolean isSat) {
            this.eventName = eventName;
            this.eventStartDt = eventStartDt;
            this.eventEndDt = eventEndDt;
            this.eventLocation = eventLocation;
            this.eventDescription = eventDescription;
            this.eventRepeatEndDt = eventRepeatEndDt;
            this.eventRecurrence = eventRecurrence;
            this.eventRepeatEvery = eventRepeatEvery;
            this.isSun = isSun;
            this.isMon = isMon;
            this.isTues = isTues;
            this.isWed = isWed;
            this.isThurs = isThurs;
            this.isFri = isFri;
            this.isSat = isSat;
            this.eventID= eventID;
        }
    }
    private static EventModel theModel = null;
    public static EventModel getSingleton(){
        if (theModel == null){
            theModel = new EventModel();
        }
        return theModel;
    }

    public ArrayList<Events> eventsList;
    private EventModel(){
        eventsList = new ArrayList<Events>();
    }

}
