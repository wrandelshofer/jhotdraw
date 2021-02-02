/*
 * @(#)MLKeyword.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.modeler.figure;

/**
 * This enum lists UML and SySML keywords.
 * <p>
 * While the modeler supports an open ended set of keywords,
 * this enum is used as a source for keywords, that are used
 * in templates that the modeler provides.
 */
public enum MLKeyword {
    // UML
    ABSTRACTION("abstraction"),// UML, dashed-line label
    ACCESS("access"),// UML, dashed-line label
    ACTIVTIY("activity"),// UML, box header
    ACTOR("actor"),// UML, box header
    APPLY("apply"),// UML, dashed-line label
    ARTIFACT("artifact"),// UML, box header
    CENTRAL_BUFFER("centralBuffer"),// UML, box header
    BIND("bind"),// UML, dashed-line label
    COLLABORATION("collaboration"),// UML, box header
    COMPONENT("component"),// UML, box header
    DATASTORE("datastore"),// UML, box header
    DATA_TYPE("dataType"),// UML, box header
    DECISION_INPUT("decisionInput"),// UML, note label
    DECISION_INPUT_FLOW("decisionInputFlow"),// UML, line label
    DEPLY("deploy"),// UML, dashed-line label
    DEPLOYMENT_SPEC("deploymentSpec"),// UML, box header
    DEVICE("device"),// UML, box header
    ELEMENT_ACCESS("elementAccess"),//dashed-line label
    ELEMENT_IMPORT("elementImport"),//dashed-line label
    ENUMERATION("enumeration"),//box header
    EXECUTION_ENVIRONMENT("executionEnvironment"),//box header
    EXTEND("extend"),//dashed-line label
    EXTENDED("extended"),//after name
    EXTERNAL("external"),// UML, swimlane header
    FINAL("final"),// UML, after name
    FLOW("flow"),// UML, dashed-line label
    IMPORT("import"),// UML, dashed-line label
    INCLUDE("include"),// UML, dashed-line label
    INFORMATION("information"),//box header
    INTERFACE("interface"),//box header
    ITERATIVE("iterative"),//top left corner
    LOCAL_POSTCONDITION("localPostcondition"),// UML, box header
    LOCAL_PRECONDITION("localPrecondition"),// UML, box header
    MANIFEST("manifest"),// UML, dashed-line label
    MERGE("merge"),//dashed-line label
    MODEL("model"),// UML, box header
    MULTICAST("multicast"),// UML, line label
    MULTIRECEIVE("multireceive"),// UML, line label
    OCCURRENCE("occurrence"),// UML, dashed-line label
    PARALLEL("parallel"),// UML, top left corner
    POSTCONDITOIN("postcondition"),// UML, box header
    PROTOCOL("protocol"),// UML, after name/box header
    PRECONDITION("precondition"),// UML, box header
    PRIMITIVE("primitive"),// UML, box header
    PROFILE("profile"),// UML, box header
    REFERENCE("reference"),// UML, dashed-line label
    REPRESENTATION("representation"),//dashed-line label
    SELECTION("selection"),// UML, note label
    SIGNAL("signal"),// UML, box header
    SINGLE_EXECUTOIN("singleExecution"),// UML, inside box
    STATEMACHINE("statemachine"),// UML, box header
    STEREOTYPE("stereotype"),// UML, box header
    STREAM("stream"),// UML, top left corner
    STRICT("strict"),// UML, dashed-line label
    STRUCTURED("structured"),// UML, box header
    SUBSTITUTE("substitue"),// UML, dashed-line label
    TRANSFORMATION("transformation"),// UML, note label
    USE("use"),// UML, dashed-line label

    // SysML Requirement diagram
    REQUIREMENT("requirement"),//SySML, box header
    NAMED_ELEMENT("namedElement"),//SySML, box header
    CONNECTOR("connector"),//SySML, before name
    BLOCK("block"),//SySML, box header

    // ML keywords that are not actually keywords in SySML nor UML but should be
    CLASS("class"),//~UML, box header
    CONTAINMENT("containment"),//~SySML, end-crosshair line label
    PART("part"),//~SySML, start-black-diamond line label
    SHARED("shared"),//~SySML, start-white-diamond line label
    GENERALIZATION("generalization"),//~UML, end-white-triangle line label
    DEPENDENCY("dependency"),//~UML, end-arrow line label
    PROPERTIES("properties"),//~SysML, compartment header
    PARTS("parts"),//~SysML, compartment header
    REFERENCES("references"),//~SysML, compartment header
    CONSTRAINTS("constraints"),//~SysML, compartment header
    PORTS("ports"),//~SysML, compartment header
    VALUES("values"),//~SysML, compartment header
    ATTRIBUTES("attributes"),//~UML, compartment header
    OPERATIONS("operations"),//~UML, compartment header
    ;

    private final String name;

    MLKeyword(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
