package it.csi.mddtools.servicegen.cxf.genutils;

import it.csi.mddtools.servicedef.Operation;
import it.csi.mddtools.servicedef.Param;
import it.csi.mddtools.typedef.Entity;
import it.csi.mddtools.typedef.Feature;
import it.csi.mddtools.typedef.PrimitiveType;
import it.csi.mddtools.typedef.Type;
import it.csi.mddtools.typedef.TypedArray;

import java.util.List;

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
		if (type instanceof Entity) {
			for (Feature f : ((Entity) type).getFeatures()) {
				if (hasAttachment(f.getType())) {
					return true;
				}
			}
		} else if (type instanceof TypedArray) {
			if (hasAttachment(((TypedArray) type).getComponentType())) {
				return true;
			}
		} else if (type instanceof PrimitiveType) {
			if (type.getName().contains("64Binary")) {
				return true;
			}
		}
		return false;
	}
	
}
