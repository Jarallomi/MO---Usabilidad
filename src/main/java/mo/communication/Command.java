package mo.communication;

public class Command {
    /*
        RECORDINGS CONTROLS COMMANDS
    */
    public static final String CANCEL_RECORDING = "cancelRecording";
    public static final String PAUSE_RESUME_RECORDING = "pauseResumeRecording";
    public static final String STOP_RECORDING = "stopRecording";
    public static final String GET_RECORDING_STATE = "getRecordingState";
    public static final String RECORDING_STATE_R = "recordingStateR";
    
    /*
        STREAMING
    */
    public static final String START_STREAMING = "startStreaming";
    public static final String STOP_STREAMING = "stopStreaming";
    public static final String CHANGE_QUALITY_STREAMING = "changeQualityStreaming";
    public static final String DATA_STREAMING = "dataStreaming";
   
    /*
        PETITION AND RESPONSE'S COMMANDS
    */
    public static final String GET_ACTIVE_PLUGINS = "gap";
    public static final String GET_ACTIVE_PLUGINS_RESPONSE = "gapR";
    
    public static final String GET_PORTS = "gp";
    public static final String GET_PORTS_RESPONSE = "gpR";
    public static final String END_CONNECTION = "endc";
    
    public static final String DIRECT_CONFIGS = "sendDConfigs";
    
    //GET CAMARA DISPONIBLE????
    
    /*
        CHAT
    */
    public static final String MSG_SERVER_TO_CLIENT = "msgSTC";
    public static final String MSG_CLIENT_TO_SERVER = "msgCTS";

    /*
        NOTES
    */
    public static final String TAKE_NOTE = "takeNote";
    public static final String TIMESTAMP_RESPONSE = "timestampMOR";
    public static final String GET_TIMESTAMP = "getTimestampMO";
    
}
