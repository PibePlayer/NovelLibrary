package io.github.gmathi.novellibrary.cleaner

import io.github.gmathi.novellibrary.dataCenter
import org.jsoup.nodes.Document

class ActiveTranslationHelper : HtmlHelper() {
    override fun additionalProcessing(doc: Document) {
        // Grab the CSS that contains the rest of chapter text and preserve it.
        val cssChapter = doc.select("div.entry-content>style").outerHtml()
        
        removeCSS(doc)
        doc.head()?.getElementsByTag("style")?.remove()
        doc.head()?.getElementsByTag("link")?.remove()

        val contentElement = doc.select("div.entry-content")

        contentElement.prepend("<h4>${getTitle(doc)}</h4><br>")

        if (!dataCenter.enableDirectionalLinks)
            doc.select("div.nnl_container")?.remove()

        if (!dataCenter.showChapterComments) {
            doc.getElementById("comments")?.remove()
        }

        doc.body().children().remove()
        doc.body().classNames().forEach { doc.body().removeClass(it) }
        doc.body().append(contentElement?.outerHtml())
        doc.body().append(cssChapter)
        // Restore user-select
        // Since a lot of text is based on CSS pseudoelements, text selection is still broken,
        // but at least it is selectable somewhat.
        doc.body().append(
            """
            <style>
                *,*::before,*::after {
                    user-select: initial !important;
                    
                    top: initial!important;
                    bottom: initial!important;
                    left: initial!important;
                    right: initial!important;
                }
            </style>
            """.trimIndent()
        )
        doc.getElementById("custom-background-css")?.remove()
    }
}