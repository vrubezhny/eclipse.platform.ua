/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ua.tests.help.other;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.help.ITopic;
import org.eclipse.help.internal.Topic;
import org.eclipse.help.internal.base.HelpEvaluationContext;
import org.eclipse.ua.tests.help.util.DocumentCreator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TopicTest extends TestCase {

	private static final String ECLIPSE_HREF = "http://www.eclipse.org";
	private static final String ECLIPSE = "eclipse";
	private static final String BUGZILLA = "bugzilla";
	private static final String BUGZILLA_HREF = "https://bugs.eclipse.org/bugs/";
	private static final String INVALID_INSTALLED = "<with variable=\"platform\">" +
	"<test property=\"org.eclipse.core.runtime.isBundleInstalled\" args=\"org.eclipse.ui.invalid\"/></with>";
	private static final String CS_INSTALLED = "<with variable=\"platform\">" +
	    "<test property=\"org.eclipse.core.runtime.isBundleInstalled\" args=\"org.eclipse.ui.cheatsheets\"/></with>";
	private static final String ENABLEMENT_CHEATSHEETS = "<enablement>" + CS_INSTALLED + "</enablement>";
	private static final String ENABLEMENT_INVALID = "<enablement>" +  INVALID_INSTALLED  + "</enablement>";
	private static final String FILTER_IN = "<filter name = \"plugin\" value = \"org.eclipse.ua.tests\"/>";
	private static final String FILTER_OUT = "<filter name = \"plugin\" value = \"org.eclipse.ua.invalid\"/>";
	private static final String NEGATED_FILTER_IN = "<filter name = \"plugin\" value = \"!org.eclipse.ua.tests\"/>";
	private static final String NEGATED_FILTER_OUT = "<filter name = \"plugin\" value = \"!org.eclipse.ua.invalid\"/>";
	private static final String TOPIC_END = "</topic>";
	private static final String TOPIC_HEAD_ECLIPSE = "<topic href=\"http://www.eclipse.org\" label=\"eclipse\">";
	private final String TOPIC_ECLIPSE = "<topic href=\"http://www.eclipse.org\" label=\"eclipse\"/>";
	private final String TOPIC_BUGZILLA = "<topic href=\"https://bugs.eclipse.org/bugs/\" label=\"bugzilla\"/>";
	
	private final String TOPIC_WITH_ENABLEMENT = TOPIC_HEAD_ECLIPSE + ENABLEMENT_CHEATSHEETS + TOPIC_END;
	private final String TOPIC_NOT_ENABLED = TOPIC_HEAD_ECLIPSE + ENABLEMENT_INVALID + TOPIC_END;
	private final String TOPIC_FILTER_IN = TOPIC_HEAD_ECLIPSE + FILTER_IN + TOPIC_END;
	private final String TOPIC_FILTER_OUT = TOPIC_HEAD_ECLIPSE + FILTER_OUT + TOPIC_END;
	private final String TOPIC_FILTER_MIXED = TOPIC_HEAD_ECLIPSE + FILTER_IN + FILTER_OUT + TOPIC_END;
	private final String TOPIC_OLD_FILTER = "<topic filter=\"plugin=org.eclipse.ua.tests\" href=\"www.eclipse.org\"" 
	    + " label=\"Transformations and transformation configurations\"/>";
	private final String TOPIC_OLD_FILTER_DISABLED = "<topic filter=\"plugin=org.eclipse.ua.invalid\" href=\"www.eclipse.org\"" 
	    + " label=\"Transformations and transformation configurations\"/>";
	private final String TOPIC_OLD_FILTER_IN__NEGATED = "<topic filter=\"plugin!=org.eclipse.ua.tests\" href=\"www.eclipse.org\"" 
	    + " label=\"Transformations and transformation configurations\"/>";
	private final String TOPIC_OLD_FILTER_OUT_NEGATED = "<topic filter=\"plugin!=org.eclipse.ua.invalid\" href=\"www.eclipse.org\"" 
	    + " label=\"Transformations and transformation configurations\"/>";
	private final String TOPIC_WITH_CHILD = TOPIC_HEAD_ECLIPSE + TOPIC_BUGZILLA + TOPIC_END;
	
	public static Test suite() {
		return new TestSuite(TopicTest.class);
	}

	private Topic createTopic(final String topicSource) {
		Topic topic;
		Document doc;
		try {
		    doc = DocumentCreator.createDocument(topicSource);
		} catch (Exception e) {
			fail("Caught Exception");
			doc = null;
		}
		topic = new Topic((Element) doc.getFirstChild());
		return topic;
	}

	public void testSimpleTopic() {
		Topic topic;
		topic = createTopic(TOPIC_ECLIPSE);
		assertEquals(ECLIPSE, topic.getLabel());
		assertEquals(ECLIPSE_HREF, topic.getHref());
	}

	public void testCopySimpleTopic() {
		Topic topic1;
		topic1 = createTopic(TOPIC_ECLIPSE);
		Topic topic2 = new Topic(topic1);
		assertEquals(ECLIPSE, topic1.getLabel());
		assertEquals(ECLIPSE_HREF, topic1.getHref());
		assertEquals(ECLIPSE, topic2.getLabel());
		assertEquals(ECLIPSE_HREF, topic2.getHref());
	}
	
	public void testCopyTopicWithChild() {
		Topic topic1;
		topic1 = createTopic(TOPIC_WITH_CHILD);
		Topic topic2 = new Topic(topic1);

		assertEquals(1, topic1.getSubtopics().length);
		Topic child1 = (Topic)topic1.getSubtopics()[0];
		assertEquals(BUGZILLA, child1.getLabel());
		assertEquals(BUGZILLA_HREF, child1.getHref());
		
		assertEquals(1, topic2.getSubtopics().length);
		Topic child2 = (Topic)topic2.getSubtopics()[0];
		assertEquals(BUGZILLA, child2.getLabel());
		assertEquals(BUGZILLA_HREF, child2.getHref());
		assertEquals(1, topic2.getSubtopics().length);
	}
	
	/*
	 * Disabled, see Bug 210024 � [Help] Topic element problems constructing from an ITopic
	public void testCopyTopicWithChildRemoveChild() {
		Topic topic1;
		topic1 = createTopic(TOPIC_WITH_CHILD);
		Topic topic2 = new Topic(topic1);
		assertEquals(1, topic1.getSubtopics().length);
		Topic child1 = (Topic)topic1.getSubtopics()[0];
		assertEquals(1, topic2.getSubtopics().length);
		topic1.removeChild(child1);
		assertEquals(0, topic1.getSubtopics().length);
		assertEquals(1, topic2.getSubtopics().length);
	}
	*/
	
	/*
	 * Test the assumption that when a topic is created from another topic not only
	 * the topic but all the children are recursively copied
	 */
	/*
	 * Disabled, see Bug 210024 � [Help] Topic element problems constructing from an ITopic
	public void testCopyTopicWithChildCheckParents() {
		Topic topic1;
		topic1 = createTopic(TOPIC_WITH_CHILD);
		Topic topic2 = new Topic(topic1);
		assertEquals(ECLIPSE, topic1.getLabel());
		assertEquals(ECLIPSE_HREF, topic1.getHref());
		assertEquals(1, topic1.getSubtopics().length);
		Topic child1 = (Topic)topic1.getSubtopics()[0];
		assertTrue(child1.getParentElement() == topic1);
		assertEquals(ECLIPSE, topic2.getLabel());
		assertEquals(ECLIPSE_HREF, topic2.getHref());
		assertEquals(1, topic2.getSubtopics().length);
		Topic child2 = (Topic)topic1.getSubtopics()[0];
		assertTrue(child2.getParentElement() == topic2);
	}
	*/

	public void testEnabledTopic() {
		Topic topic;
		topic = createTopic(TOPIC_WITH_ENABLEMENT);
		assertTrue(topic.isEnabled(HelpEvaluationContext.getContext()));
	}

	public void testDisabledTopic() {
		Topic topic;
		topic = createTopic(TOPIC_NOT_ENABLED);
		assertFalse(topic.isEnabled(HelpEvaluationContext.getContext()));
	}
	
	public void testCopyDisabledTopic() {
		Topic topic1;
		topic1 = createTopic(TOPIC_NOT_ENABLED);
		Topic topic2 = new Topic(topic1);
		Topic topic3 = new Topic(topic2);
		assertFalse(topic1.isEnabled(HelpEvaluationContext.getContext()));
		assertFalse(topic2.isEnabled(HelpEvaluationContext.getContext()));
		assertFalse(topic3.isEnabled(HelpEvaluationContext.getContext()));
	}
	
	public void testCompoundEnablement() {
		Topic topic;
		topic = createTopic(TOPIC_HEAD_ECLIPSE + "<enablement>"
				+ CS_INSTALLED 
				+ INVALID_INSTALLED 
				+ "</enablement>" + TOPIC_END);
		assertFalse(topic.isEnabled(HelpEvaluationContext.getContext()));
		topic = createTopic(TOPIC_HEAD_ECLIPSE + "<enablement><and>" 
				+ INVALID_INSTALLED 
				+ CS_INSTALLED 
				+ "</and></enablement>" + TOPIC_END);
		assertFalse(topic.isEnabled(HelpEvaluationContext.getContext()));
	}

	public void testOldStyleEnablement() {
		Topic topic;
		topic = createTopic(TOPIC_OLD_FILTER);
		assertTrue(topic.isEnabled(HelpEvaluationContext.getContext()));
	}

	public void testOldStyleDisabled() {
		Topic topic;
		topic = createTopic(TOPIC_OLD_FILTER_DISABLED);
		assertFalse(topic.isEnabled(HelpEvaluationContext.getContext()));
	}
	
	public void testOldStyleNegated() {
		Topic topic;
		topic = createTopic(TOPIC_OLD_FILTER_IN__NEGATED);
		assertFalse(topic.isEnabled(HelpEvaluationContext.getContext()));
		topic = createTopic(TOPIC_OLD_FILTER_OUT_NEGATED);
		assertTrue(topic.isEnabled(HelpEvaluationContext.getContext()));
	}
	
	public void testCopyOldStyleDisabled() {
		Topic topic1;
		topic1 = createTopic(TOPIC_OLD_FILTER_DISABLED);
		Topic topic2 = new Topic(topic1);
		Topic topic3 = new Topic(topic2);
		assertFalse(topic1.isEnabled(HelpEvaluationContext.getContext()));
		assertFalse(topic2.isEnabled(HelpEvaluationContext.getContext()));
		assertFalse(topic3.isEnabled(HelpEvaluationContext.getContext()));
	}

	public void testFilterIn() {
		Topic topic;
		topic = createTopic(TOPIC_FILTER_IN);
		assertTrue(topic.isEnabled(HelpEvaluationContext.getContext()));
	}

	public void testFilterOut() {
		Topic topic;
		topic = createTopic(TOPIC_FILTER_OUT);
		assertFalse(topic.isEnabled(HelpEvaluationContext.getContext()));
	}
	
	public void testFilterMixed() {
		Topic topic;
		topic = createTopic(TOPIC_FILTER_MIXED);
		assertFalse(topic.isEnabled(HelpEvaluationContext.getContext()));
	}
	
	public void testNegatedFilters() {
		Topic topic;
		topic = createTopic(TOPIC_HEAD_ECLIPSE + NEGATED_FILTER_IN + TOPIC_END);
		assertFalse(topic.isEnabled(HelpEvaluationContext.getContext()));
		topic = createTopic(TOPIC_HEAD_ECLIPSE + NEGATED_FILTER_OUT + TOPIC_END);
		assertTrue(topic.isEnabled(HelpEvaluationContext.getContext()));
	}

	public void testCopyFilterOut() {
		Topic topic1;
		topic1 = createTopic(TOPIC_FILTER_OUT);
		Topic topic2 = new Topic(topic1);
		Topic topic3 = new Topic(topic2);
		assertFalse(topic1.isEnabled(HelpEvaluationContext.getContext()));
		assertFalse(topic2.isEnabled(HelpEvaluationContext.getContext()));
		assertFalse(topic3.isEnabled(HelpEvaluationContext.getContext()));
	}

	public void testUserTopic() {
		UserTopic u1 = new UserTopic(ECLIPSE, ECLIPSE_HREF, false);
		Topic t1 = new Topic(u1);
		assertEquals(ECLIPSE, t1.getLabel());
		assertEquals(ECLIPSE_HREF, t1.getHref());
		assertFalse(t1.isEnabled(HelpEvaluationContext.getContext()));
	}

	public void testCopyFilteredUserTopic() {
		UserTopic u1 = new UserTopic(ECLIPSE, ECLIPSE_HREF, false);
		Topic t1 = new Topic(u1);
		Topic t2 = new Topic(t1);
		assertEquals(ECLIPSE, t1.getLabel());
		assertEquals(ECLIPSE_HREF, t1.getHref());
		assertFalse(t1.isEnabled(HelpEvaluationContext.getContext()));
		assertEquals(ECLIPSE, t2.getLabel());
		assertEquals(ECLIPSE_HREF, t2.getHref());
		assertFalse(t2.isEnabled(HelpEvaluationContext.getContext()));
	}
	
	/*
	 * Disabled, see Bug 210024 [Help] Topic element problems constructing from an ITopic
	public void testUserTopicWithFilteredChildren() {
		UserTopic u1 = new UserTopic(ECLIPSE, ECLIPSE_HREF, true);
		UserTopic u2 = new UserTopic(BUGZILLA, BUGZILLA_HREF, false);
		u1.addTopic(u2);
		Topic t1 = new Topic(u1);
		assertEquals(ECLIPSE, t1.getLabel());
		assertEquals(ECLIPSE_HREF, t1.getHref());
		assertTrue(t1.isEnabled(HelpEvaluationContext.getContext()));
		assertEquals(1, t1.getChildren().length);
		ITopic t2 = t1.getSubtopics()[0];
		assertEquals(BUGZILLA, t2.getLabel());
		assertEquals(BUGZILLA_HREF, t2.getHref());
		assertFalse(t2.isEnabled(HelpEvaluationContext.getContext()));
	}
	*/
	
	public void testCopyUserTopicWithChildren() {
		UserTopic u1 = new UserTopic(ECLIPSE, ECLIPSE_HREF, true);
		UserTopic u2 = new UserTopic(BUGZILLA, BUGZILLA_HREF, true);
		u1.addTopic(u2);
		Topic t1 = new Topic(u1);
		Topic t2 = new Topic(t1);

		assertEquals(ECLIPSE, t1.getLabel());
		assertEquals(ECLIPSE_HREF, t1.getHref());
		assertTrue(t1.isEnabled(HelpEvaluationContext.getContext()));
		assertEquals(1, t1.getChildren().length);
		ITopic t1s = t1.getSubtopics()[0];
		assertEquals(BUGZILLA, t1s.getLabel());
		assertEquals(BUGZILLA_HREF, t1s.getHref());
		
		assertEquals(ECLIPSE, t2.getLabel());
		assertEquals(ECLIPSE_HREF, t2.getHref());
		assertTrue(t2.isEnabled(HelpEvaluationContext.getContext()));
		assertEquals(1, t2.getChildren().length);
		ITopic t2s = t2.getSubtopics()[0];
		assertEquals(BUGZILLA, t2s.getLabel());
		assertEquals(BUGZILLA_HREF, t2s.getHref());
	}
		
}
