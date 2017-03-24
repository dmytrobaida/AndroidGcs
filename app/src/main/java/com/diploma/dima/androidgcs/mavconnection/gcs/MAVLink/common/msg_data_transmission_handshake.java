/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */

// MESSAGE DATA_TRANSMISSION_HANDSHAKE PACKING
package com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.common;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.MAVLinkPacket;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.Messages.MAVLinkMessage;
import com.diploma.dima.androidgcs.mavconnection.gcs.MAVLink.Messages.MAVLinkPayload;

/**
* 
*/
public class msg_data_transmission_handshake extends MAVLinkMessage{

    public static final int MAVLINK_MSG_ID_DATA_TRANSMISSION_HANDSHAKE = 130;
    public static final int MAVLINK_MSG_LENGTH = 13;
    private static final long serialVersionUID = MAVLINK_MSG_ID_DATA_TRANSMISSION_HANDSHAKE;


      
    /**
    * total data size in bytes (set on ACK only)
    */
    public long size;
      
    /**
    * Width of a matrix or image
    */
    public int width;
      
    /**
    * Height of a matrix or image
    */
    public int height;
      
    /**
    * number of packets beeing sent (set on ACK only)
    */
    public int packets;
      
    /**
    * type of requested/acknowledged data (as defined in ENUM DATA_TYPES in MAVLink/include/mavlink_types.h)
    */
    public short type;
      
    /**
    * payload size per packet (normally 253 byte, see DATA field size in message ENCAPSULATED_DATA) (set on ACK only)
    */
    public short payload;
      
    /**
    * JPEG quality out of [1,100]
    */
    public short jpg_quality;
    

    /**
    * Generates the payload for a MAVLink message for a message of this type
    * @return
    */
    public MAVLinkPacket pack(){
        MAVLinkPacket packet = new MAVLinkPacket(MAVLINK_MSG_LENGTH);
        packet.sysid = 255;
        packet.compid = 190;
        packet.msgid = MAVLINK_MSG_ID_DATA_TRANSMISSION_HANDSHAKE;
              
        packet.payload.putUnsignedInt(size);
              
        packet.payload.putUnsignedShort(width);
              
        packet.payload.putUnsignedShort(height);
              
        packet.payload.putUnsignedShort(packets);
              
        packet.payload.putUnsignedByte(type);
              
        packet.payload.putUnsignedByte(payload);
              
        packet.payload.putUnsignedByte(jpg_quality);
        
        return packet;
    }

    /**
    * Decode a data_transmission_handshake message into this class fields
    *
    * @param payload The message to decode
    */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
              
        this.size = payload.getUnsignedInt();
              
        this.width = payload.getUnsignedShort();
              
        this.height = payload.getUnsignedShort();
              
        this.packets = payload.getUnsignedShort();
              
        this.type = payload.getUnsignedByte();
              
        this.payload = payload.getUnsignedByte();
              
        this.jpg_quality = payload.getUnsignedByte();
        
    }

    /**
    * Constructor for a new message, just initializes the msgid
    */
    public msg_data_transmission_handshake(){
        msgid = MAVLINK_MSG_ID_DATA_TRANSMISSION_HANDSHAKE;
    }

    /**
    * Constructor for a new message, initializes the message with the payload
    * from a MAVLink packet
    *
    */
    public msg_data_transmission_handshake(MAVLinkPacket mavLinkPacket){
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_DATA_TRANSMISSION_HANDSHAKE;
        unpack(mavLinkPacket.payload);        
    }

                  
    /**
    * Returns a string with the MSG name and data
    */
    public String toString(){
        return "MAVLINK_MSG_ID_DATA_TRANSMISSION_HANDSHAKE -"+" size:"+size+" width:"+width+" height:"+height+" packets:"+packets+" type:"+type+" payload:"+payload+" jpg_quality:"+jpg_quality+"";
    }
}
        