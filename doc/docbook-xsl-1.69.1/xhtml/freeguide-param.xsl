<?xml version="1.0" encoding="US-ASCII"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="1.0">

<!-- 
    Freeguide-specific parameters
    Override the ones in param.xsl
    @author Christian Weiske <cweiske@cweiske.de>

Changes to the original docbook stylesheets:
- I modified qandaset.xsl because there was no matching
    param to disable the extra toc for the set.
- docbook.xsl/template="user.header.navigation" has been moved to
    freeguide-things.xsl 
- docbook.xsl/template="user.head.content" has been moved to
    freeguide-things.xsl to
    support "contents", "help", "author" and "copyright" links
- autotoc.xsl/template="make.toc" doesn't generate the "Table of Contents"
    text any more.
< titlepage.templates.xsl/template="book.titlepage.separator" doesn't
    include the <hr/> tag any more
-->

<xsl:param name="use.id.as.filename" select="'1'"/>
<xsl:param name="base.dir" select="'../build/doc/html/'"/>
<!-- removing the book line causes the initial toc not to be generated -->
<xsl:param name="generate.toc">
appendix  toc,title
article/appendix  nop
article   toc,title
chapter   toc,title
part      toc,title
preface   toc,title
qandadiv  toc
qandaset  toc
reference toc,title
sect1     toc
sect2     toc
sect3     toc
sect4     toc
sect5     toc
section   toc
set       toc,title
</xsl:param>

<xsl:param name="links.toc"         select="'toc'"/>
<xsl:param name="links.help"        select="'help'"/>
<xsl:param name="links.author"      select="'contributors'"/>
<xsl:param name="links.copyright"   select="'license'"/>
<xsl:param name="toc.title.p"       select="''"/>

<!-- remove the <hr> tags from header and footer -->
<xsl:param name="header.rule" select="0"/>
<xsl:param name="footer.rule" select="0"/>


</xsl:stylesheet>
