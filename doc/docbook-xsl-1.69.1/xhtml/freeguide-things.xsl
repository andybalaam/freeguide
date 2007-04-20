<?xml version="1.0" encoding="US-ASCII"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="1.0">

<!-- 
    some additions to customize the freeguide manual
    @author Christian Weiske <cweiske@cweiske.de>
-->

<xsl:template match="autotoc">
    <xsl:call-template name="make.toc">
        <xsl:with-param name="toc-context" select="/book"/>
        <xsl:with-param name="nodes" select="/"/>
        <xsl:with-param name="toc.title.p" select="false()"/>
    </xsl:call-template>
</xsl:template>

<!--
 That allows us to have an extra navigation line at
 the top of the page. The contents are defined in
 a special section in the manual:
   <toc id="navigation">
    <tocchap linked="freeguide-manual" title="Home"/>
    <tocchap linked="downinst"/>
    ...
   </toc>
 If you add a "title" attribute, it's used as link text.
 If not, the id's <title> is used.
-->
<xsl:template name="user.header.navigation">

  <div style="float:right" class="sourceforgelogo">
    <a href="http://sourceforge.net/projects/freeguide-tv/">
       <img src="http://sourceforge.net/sflogo.php?group_id=35309&amp;type=1" width="88" height="31" border="0" alt="SourceForge Logo"/> 
    </a>
  </div>

  <div class="navigation">
    <xsl:variable name="id_this" select="@id"/>
    <xsl:variable name="id_levelup" select="parent::node()/@id"/>
    <xsl:for-each select="//toc[@id='navigation']/tocchap">
        <xsl:variable name="id" select="@linked"/>
        <xsl:variable name="chapter" select="//*[@id=$id]"/>
        <a>
            <xsl:attribute name="href">
                <xsl:call-template name="href.target">
                    <xsl:with-param name="object" select="$chapter"/>
                </xsl:call-template>
            </xsl:attribute>
            <xsl:if test="$id_this = $id">
                <xsl:attribute name="class">active</xsl:attribute>
            </xsl:if>
            <xsl:choose>
                <xsl:when test="@title != ''">
                    <xsl:value-of select="@title"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$chapter/title"/>
                </xsl:otherwise>
            </xsl:choose>
<!--            <xsl:message>
              F:<xsl:value-of select="parent::node()[@id=$id]/title"/>
            </xsl:message>-->
        </a>
        <xsl:if test="position()!=last()"> | </xsl:if>
    </xsl:for-each>
  </div>

</xsl:template>


<!-- additional tags in the <head> section -->
<xsl:template name="user.head.content">
    <link rel="stylesheet" type="text/css" href="stylesheet.css"/>

    <!-- TOC link -->
    <xsl:if test="$links.toc!=''">
      <link rel="contents">
        <xsl:attribute name="href">
          <xsl:call-template name="href.target">
            <xsl:with-param name="object" select="//chapter[@id=$links.toc]"/>
          </xsl:call-template>
        </xsl:attribute>
        <xsl:attribute name="title">
          <xsl:apply-templates select="//chapter[@id=$links.toc]" mode="object.title.markup.textonly"/>
        </xsl:attribute>
      </link>
    </xsl:if>

    <!-- Authors link -->
    <xsl:if test="$links.author!=''">
      <link rel="author">
        <xsl:attribute name="href">
          <xsl:call-template name="href.target">
            <xsl:with-param name="object" select="//chapter[@id=$links.author]"/>
          </xsl:call-template>
        </xsl:attribute>
        <xsl:attribute name="title">
          <xsl:apply-templates select="//chapter[@id=$links.author]" mode="object.title.markup.textonly"/>
        </xsl:attribute>
      </link>
    </xsl:if>

    <!-- Help link -->
    <xsl:if test="$links.help!=''">
      <link rel="help">
        <xsl:attribute name="href">
          <xsl:call-template name="href.target">
            <xsl:with-param name="object" select="//chapter[@id=$links.help]"/>
          </xsl:call-template>
        </xsl:attribute>
        <xsl:attribute name="title">
          <xsl:apply-templates select="//chapter[@id=$links.help]" mode="object.title.markup.textonly"/>
        </xsl:attribute>
      </link>
    </xsl:if>

    <!-- Copyright/license link -->
    <xsl:if test="$links.copyright!=''">
      <link rel="copyright">
        <xsl:attribute name="href">
          <xsl:call-template name="href.target">
            <xsl:with-param name="object" select="//chapter[@id=$links.copyright]"/>
          </xsl:call-template>
        </xsl:attribute>
        <xsl:attribute name="title">
          <xsl:apply-templates select="//chapter[@id=$links.copyright]" mode="object.title.markup.textonly"/>
        </xsl:attribute>
      </link>
    </xsl:if>
</xsl:template>


</xsl:stylesheet>

