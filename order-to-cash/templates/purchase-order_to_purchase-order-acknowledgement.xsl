<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:uuid="java.util.UUID">
	<xsl:output method="xml" encoding="UTF-8" omit-xml-declaration="yes"/>
	<xsl:param name="id"/>
	<xsl:template match="/purchaseOrder">
		<purchaseOrderAcknowledgement>
			<xsl:attribute name="id">
				<xsl:value-of select="uuid:randomUUID()"></xsl:value-of>
			</xsl:attribute>
			<xsl:attribute name="correlationId">
				<xsl:value-of select="$id"/>
			</xsl:attribute>
			<purchaseOrder>
				<xsl:attribute name="id">
					<xsl:value-of select="@id"/>
				</xsl:attribute>
				<buyer>
					<xsl:attribute name="name">
						<xsl:value-of select="buyer/@name"/>
					</xsl:attribute>
				</buyer>
				<supplier>
					<xsl:attribute name="name">
						<xsl:value-of select="supplier/@name"/>
					</xsl:attribute>
				</supplier>
			</purchaseOrder>			
		</purchaseOrderAcknowledgement>
	</xsl:template>
</xsl:stylesheet>