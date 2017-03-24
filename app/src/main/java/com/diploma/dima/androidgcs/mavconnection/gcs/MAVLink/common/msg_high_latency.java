/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */

// MESSAGE HIGH_LATENCY PACKING
package com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.common;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.MAVLinkPacket;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.Messages.MAVLinkMessage;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.Messages.MAVLinkPayload;

/**
* Message appropriate for high latency connections like Iridium
*/
public class msg_high_latency extends MAVLinkMessage{

    public static final int MAVLINK_MSG_ID_HIGH_LATENCY = 234;
    public static final int MAVLINK_MSG_LENGTH = 40;
    private static final long serialVersionUID = MAVLINK_MSG_ID_HIGH_LATENCY;


      
    /**
    * A bitfield for use for autopilot-specific flags.
    */
    public long custom_mode;
      
    /**
    * Latitude, expressed as degrees * 1E7
    */
    public int latitude;
      
    /**
    * Longitude, expressed as degrees * 1E7
    */
    public int longitude;
      
    /**
    * roll (centidegrees)
    */
    public short roll;
      
    /**
    * pitch (centidegrees)
    */
    public short pitch;
      
    /**
    * heading (centidegrees)
    */
    public int heading;
      
    /**
    * heading setpoint (centidegrees)
    */
    public short heading_sp;
      
    /**
    * Altitude above mean sea level (meters)
    */
    public short altitude_amsl;
      
    /**
    * Altitude setpoint relative to the home position (meters)
    */
    public short altitude_sp;
      
    /**
    * distance to target (meters)
    */
    public int wp_distance;
      
    /**
    * System mode bitfield, see MAV_MODE_FLAG ENUM in MAVLink/include/mavlink_types.h
    */
    public short base_mode;
      
    /**
    * The landed state. Is set to MAV_LANDED_STATE_UNDEFINED if landed state is unknown.
    */
    public short landed_state;
      
    /**
    * throttle (percentage)
    */
    public byte throttle;
      
    /**
    * airspeed (m/s)
    */
    public short airspeed;
      
    /**
    * airspeed setpoint (m/s)
    */
    public short airspeed_sp;
      
    /**
    * groundspeed (m/s)
    */
    public short groundspeed;
      
    /**
    * climb rate (m/s)
    */
    public byte climb_rate;
      
    /**
    * Number of satellites visible. If unknown, set to 255
    */
    public short gps_nsat;
      
    /**
    * See the GPS_FIX_TYPE enum.
    */
    public short gps_fix_type;
      
    /**
    * Remaining battery (percentage)
    */
    public short battery_remaining;
      
    /**
    * Autopilot temperature (degrees C)
    */
    public byte temperature;
      
    /**
    * Air temperature (degrees C) from airspeed sensor
    */
    public byte temperature_air;
      
    /**
    * failsafe (each bit represents a failsafe where 0=ok, 1=failsafe active (bit0:RC, bit1:batt, bit2:GPS, bit3:GCS, bit4:fence)
    */
    public short failsafe;
      
    /**
    * current waypoint number
    */
    public short wp_num;
    

    /**
    * Generates the payload for a MAVLink message for a message of this type
    * @return
    */
    public MAVLinkPacket pack(){
        MAVLinkPacket packet = new MAVLinkPacket(MAVLINK_MSG_LENGTH);
        packet.sysid = 255;
        packet.compid = 190;
        packet.msgid = MAVLINK_MSG_ID_HIGH_LATENCY;
              
        packet.payload.putUnsignedInt(custom_mode);
              
        packet.payload.putInt(latitude);
              
        packet.payload.putInt(longitude);
              
        packet.payload.putShort(roll);
              
        packet.payload.putShort(pitch);
              
        packet.payload.putUnsignedShort(heading);
              
        packet.payload.putShort(heading_sp);
              
        packet.payload.putShort(altitude_amsl);
              
        packet.payload.putShort(altitude_sp);
              
        packet.payload.putUnsignedShort(wp_distance);
              
        packet.payload.putUnsignedByte(base_mode);
              
        packet.payload.putUnsignedByte(landed_state);
              
        packet.payload.putByte(throttle);
              
        packet.payload.putUnsignedByte(airspeed);
              
        packet.payload.putUnsignedByte(airspeed_sp);
              
        packet.payload.putUnsignedByte(groundspeed);
              
        packet.payload.putByte(climb_rate);
              
        packet.payload.putUnsignedByte(gps_nsat);
              
        packet.payload.putUnsignedByte(gps_fix_type);
              
        packet.payload.putUnsignedByte(battery_remaining);
              
        packet.payload.putByte(temperature);
              
        packet.payload.putByte(temperature_air);
              
        packet.payload.putUnsignedByte(failsafe);
              
        packet.payload.putUnsignedByte(wp_num);
        
        return packet;
    }

    /**
    * Decode a high_latency message into this class fields
    *
    * @param payload The message to decode
    */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
              
        this.custom_mode = payload.getUnsignedInt();
              
        this.latitude = payload.getInt();
              
        this.longitude = payload.getInt();
              
        this.roll = payload.getShort();
              
        this.pitch = payload.getShort();
              
        this.heading = payload.getUnsignedShort();
              
        this.heading_sp = payload.getShort();
              
        this.altitude_amsl = payload.getShort();
              
        this.altitude_sp = payload.getShort();
              
        this.wp_distance = payload.getUnsignedShort();
              
        this.base_mode = payload.getUnsignedByte();
              
        this.landed_state = payload.getUnsignedByte();
              
        this.throttle = payload.getByte();
              
        this.airspeed = payload.getUnsignedByte();
              
        this.airspeed_sp = payload.getUnsignedByte();
              
        this.groundspeed = payload.getUnsignedByte();
              
        this.climb_rate = payload.getByte();
              
        this.gps_nsat = payload.getUnsignedByte();
              
        this.gps_fix_type = payload.getUnsignedByte();
              
        this.battery_remaining = payload.getUnsignedByte();
              
        this.temperature = payload.getByte();
              
        this.temperature_air = payload.getByte();
              
        this.failsafe = payload.getUnsignedByte();
              
        this.wp_num = payload.getUnsignedByte();
        
    }

    /**
    * Constructor for a new message, just initializes the msgid
    */
    public msg_high_latency(){
        msgid = MAVLINK_MSG_ID_HIGH_LATENCY;
    }

    /**
    * Constructor for a new message, initializes the message with the payload
    * from a MAVLink packet
    *
    */
    public msg_high_latency(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_HIGH_LATENCY;
        unpack(mavLinkPacket.payload);        
    }

                                                    
    /**
    * Returns a string with the MSG name and data
    */
    public String toString(){
        return "MAVLINK_MSG_ID_HIGH_LATENCY -"+" custom_mode:"+custom_mode+" latitude:"+latitude+" longitude:"+longitude+" roll:"+roll+" pitch:"+pitch+" heading:"+heading+" heading_sp:"+heading_sp+" altitude_amsl:"+altitude_amsl+" altitude_sp:"+altitude_sp+" wp_distance:"+wp_distance+" base_mode:"+base_mode+" landed_state:"+landed_state+" throttle:"+throttle+" airspeed:"+airspeed+" airspeed_sp:"+airspeed_sp+" groundspeed:"+groundspeed+" climb_rate:"+climb_rate+" gps_nsat:"+gps_nsat+" gps_fix_type:"+gps_fix_type+" battery_remaining:"+battery_remaining+" temperature:"+temperature+" temperature_air:"+temperature_air+" failsafe:"+failsafe+" wp_num:"+wp_num+"";
    }
}
        