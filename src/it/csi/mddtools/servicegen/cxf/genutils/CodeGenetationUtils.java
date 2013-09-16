package it.csi.mddtools.servicegen.cxf.genutils;

import it.csi.mddtools.servicedef.Operation;
import it.csi.mddtools.servicedef.Param;
import it.csi.mddtools.servicedef.WSEncrypt;
import it.csi.mddtools.servicedef.WSEndpointChannel;
import it.csi.mddtools.servicedef.WSSecuritySpec;
import it.csi.mddtools.servicedef.WSSignature;
import it.csi.mddtools.servicedef.WSTimestamp;
import it.csi.mddtools.servicedef.WSUsernameTokenAuth;
import it.csi.mddtools.typedef.Entity;
import it.csi.mddtools.typedef.Feature;
import it.csi.mddtools.typedef.PrimitiveType;
import it.csi.mddtools.typedef.Type;
import it.csi.mddtools.typedef.TypedArray;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.util.StringUtils;

/**
 * Questa classe contiene i metodi di utilit&agrave; per il generatore <b>ServiceGen CXF</b>.
 *
 * @author Davide Martinotti
 */
public class CodeGenetationUtils {

	/**
	 * Verifica se le operazioni di un servizio definiscono tipi di dato
	 * Binary64 (ovvero se esistono degli allegati).
	 * Non appena trovo un attachment, interrompo e ritorno true.
	 * Se non trovo attachment ritorno false.
	 * 
	 * @param operations Lista delle operazioni definite da un servizio.
	 * @return true se esiste un attacment, false latrimenti.
	 */
	public static boolean hasAttachment(List<Operation> operations) {
		for (Operation op : operations) {
			// ciclo prima sui parametri
			for (Param param : op.getParams()) {
				if (hasAttachment(param.getType())) {
					return true;
				}
			}
			
			// poi verifico il tipo di ritorno
			if (hasAttachment(op.getReturnType())) {
				return true;
			}
		}
		return false;
	}

	private static boolean hasAttachment(Type type) {
		ArrayList<Type> alreadyVisited  = new ArrayList<Type>();
		return hasAttachment_internal(type, alreadyVisited);
	}
	
	private static boolean hasAttachment_internal(Type type,
			ArrayList<Type> alreadyVisited) {
		if (type instanceof Entity) {
			if (alreadyVisited.contains(type)) {
				return false;
			} else {
				alreadyVisited.add(type);
				for (Feature f : ((Entity) type).getFeatures()) {
					if (hasAttachment_internal(f.getType(), alreadyVisited)) {
						return true;
					}
				}
			}
		} else if (type instanceof TypedArray) {
			if (hasAttachment_internal(((TypedArray) type).getComponentType(), alreadyVisited)) {
				return true;
			}
		} else if (type instanceof PrimitiveType) {
			if (type.getName().contains("64Binary")) {
				return true;
			}
		}
		return false;
	}
	
	public static String ACTION_ENCRYPT = "Encrypt";
	public static String ACTION_SIGNATURE = "Signature";
	public static String ACTION_TIMESTAMP = "Timestamp";
	public static String ACTION_USERNAMETOKEN = "UsernameToken";
	public static String getActionEndPoint(WSEndpointChannel actionList){
		String actionArray = "";
		for (WSSecuritySpec action : actionList.getWsSecurity()) {
			if(action instanceof WSEncrypt)
				actionArray = String.format(actionArray+" %s ", ACTION_ENCRYPT);
			else if(action instanceof WSSignature)
				actionArray = String.format(actionArray+" %s ", ACTION_SIGNATURE);
			else if(action instanceof WSTimestamp)
				actionArray = String.format(actionArray+" %s ", ACTION_TIMESTAMP);
			else if(action instanceof WSUsernameTokenAuth)
				actionArray = String.format(actionArray+" %s ", ACTION_USERNAMETOKEN);
		}
		return actionArray;
	}
	
	public static String getTypeOfAction(WSSecuritySpec action){
		String type = "";
		if(action instanceof WSEncrypt)
			type = ACTION_ENCRYPT;
		else if(action instanceof WSSignature)
			type = ACTION_SIGNATURE;
		else if(action instanceof WSTimestamp)
			type = ACTION_TIMESTAMP;
		else if(action instanceof WSUsernameTokenAuth)
			type = ACTION_USERNAMETOKEN;
		return type;
	}
}
