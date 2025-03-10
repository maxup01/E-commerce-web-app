package org.example.backend.enumerated;

import java.util.Objects;

public enum ReturnCause {
    WRONG_SIZE, DAMAGED, LOW_QUALITY, PROBLEMS_WITH_WORK, MISLEADING_DATA, CHANGED_MIND;

    public static ReturnCause fromString(String string) {

        if(Objects.equals(string, "wrong size"))
            return WRONG_SIZE;
        else if(Objects.equals(string, "damaged"))
            return DAMAGED;
        else if(Objects.equals(string, "low quality"))
            return LOW_QUALITY;
        else if(Objects.equals(string, "problem with work"))
            return PROBLEMS_WITH_WORK;
        else if(Objects.equals(string, "misleading data"))
            return MISLEADING_DATA;
        else if(Objects.equals(string, "changed mind"))
            return CHANGED_MIND;
        else
            return null;
    }

    public static String toString(ReturnCause returnCause) {

        if(returnCause == WRONG_SIZE)
            return "wrong size";
        else if(returnCause == DAMAGED)
            return "damaged";
        else if(returnCause == LOW_QUALITY)
            return "low quality";
        else if(returnCause == PROBLEMS_WITH_WORK)
            return "problem with work";
        else if(returnCause == MISLEADING_DATA)
            return "misleading data";
        else if(returnCause == CHANGED_MIND)
            return "changed mind";
        else
            return null;
    }
}
