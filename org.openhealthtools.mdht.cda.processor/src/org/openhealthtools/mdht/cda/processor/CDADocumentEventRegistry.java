/*******************************************************************************
 * Copyright (c) 2010, 2012 Sean Muir
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sean Muir - initial API and implementation
 *
 * $Id$
 *******************************************************************************/
package org.openhealthtools.mdht.cda.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.mdht.uml.cda.ClinicalDocument;

/**
 *
 *
 *
 * CDADocumentEventRegistry maintains a collection of cda document event
 *
 * handlers and will execute all of the handlers defined for the cda
 *
 * dependencies
 *
 *
 *
 * @author Sean Muir
 *
 *
 */

public class CDADocumentEventRegistry {

	private static class CDADocumentEventProcessorComparator
			implements Comparator<CDADocumentEventProcessor<? extends ClinicalDocument>> {

		public int compare(CDADocumentEventProcessor<? extends ClinicalDocument> cdaDocumentEventProcessor0,
				CDADocumentEventProcessor<? extends ClinicalDocument> cdaDocumentEventProcessor1) {

			int result = 0;

			if (cdaDocumentEventProcessor0 != null && cdaDocumentEventProcessor1 != null) {

				result = (cdaDocumentEventProcessor0.getPriority() > cdaDocumentEventProcessor1.getPriority()
						? 1

						: (cdaDocumentEventProcessor0.getPriority() < cdaDocumentEventProcessor1.getPriority()
								? -1
								: 0));

			}

			return result;

		}

	};

	static HashMap<EClass, ArrayList<CDADocumentEventProcessor<? extends ClinicalDocument>>> documentEventRegistry = new HashMap<EClass, ArrayList<CDADocumentEventProcessor<? extends ClinicalDocument>>>();

	static CDADocumentEventProcessorComparator cdaDocumentEventProcessorComparator = new CDADocumentEventProcessorComparator();

	public static void registerCDADocumentEventProcessor(EClass cdaDocument,
			CDADocumentEventProcessor<? extends ClinicalDocument> cdaDocumentEventProcessor) {

		if (!documentEventRegistry.containsKey(cdaDocument)) {

			documentEventRegistry.put(
				cdaDocument, new ArrayList<CDADocumentEventProcessor<? extends ClinicalDocument>>());

		}

		documentEventRegistry.get(cdaDocument).add(cdaDocumentEventProcessor);

		Collections.sort(documentEventRegistry.get(cdaDocument), new CDADocumentEventProcessorComparator());

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void processCDADocumentEvent(ClinicalDocument cdaDocument) {

		for (EClass eclass : cdaDocument.eClass().getEAllSuperTypes()) {

			if (documentEventRegistry.containsKey(eclass)) {

				for (CDADocumentEventProcessor cdaDocumentEventProcess : documentEventRegistry.get(eclass)) {

					cdaDocumentEventProcess.ProcessCDADocumentEvent(cdaDocument);

				}

			}

		}

		if (documentEventRegistry.containsKey(cdaDocument.eClass())) {

			for (CDADocumentEventProcessor cdaDocumentEventProcess : documentEventRegistry.get(cdaDocument.eClass())) {

				cdaDocumentEventProcess.ProcessCDADocumentEvent(cdaDocument);

			}

		}

	}

}