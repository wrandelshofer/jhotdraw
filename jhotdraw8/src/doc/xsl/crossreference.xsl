<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ @(#)crossreference.xsl
  ~ Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns="http://docbook.org/ns/docbook"
                xmlns:db="http://docbook.org/ns/docbook">
    <xsl:output method="xml"/>

    <!-- TODO customize transformation rules
         syntax recommendation http://www.w3.org/TR/xslt
    -->
    <xsl:template match="/">
        <section xmlns="http://docbook.org/ns/docbook" version="5.0" xml:lang="en">
            <title>Cross Reference</title>
            <para>This section is empty.</para>
            <xsl:apply-templates/>
        </section>
    </xsl:template>

    <!-- suppress text and attributes -->
<xsl:template match="text()|@*"/>

<xsl:template match="db:section">
  <content comment="{key('el-by-idref', @id)}" referencedid="{@xml:id}" test="{@xml:id}"/>
   <xsl:text>&#xa;</xsl:text>
</xsl:template>

</xsl:stylesheet>
