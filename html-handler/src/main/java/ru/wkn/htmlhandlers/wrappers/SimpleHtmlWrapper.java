package ru.wkn.htmlhandlers.wrappers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import ru.wkn.RepositoryFacade;
import ru.wkn.entities.HtmlTag;
import ru.wkn.entities.JavaScriptFunction;
import ru.wkn.htmlforms.HtmlFormType;
import ru.wkn.repository.JavaScriptFunctionRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleHtmlWrapper extends HtmlWrapper {

    public SimpleHtmlWrapper(RepositoryFacade repositoryFacade) {
        super(repositoryFacade);
    }

    @Override
    public Element wrapHtmlTagsIntoForm(List<HtmlTag> htmlTags, HtmlFormType htmlFormType) {
        Element element = new Element(htmlFormType.getHtmlFormType());
        for (HtmlTag htmlTag : htmlTags) {
            Attributes attributes = new Attributes();
            for (String attribute : htmlTag.getHtmlAttributes().keySet()) {
                attributes.put(attribute, htmlTag.getHtmlAttributes().get(attribute));
            }
            element.appendChild(new Element(Tag.valueOf(htmlTag.getType().getType()), "", attributes));
        }
        return element;
    }

    @Override
    public Document wrapHtmlTagsIntoHtmlPage(List<HtmlTag> htmlTags) {
        Map<JavaScriptFunction, String> javaScriptFunctions = extractJSFunctions(htmlTags);
        StringBuilder htmlPageBuilder = new StringBuilder("<!DOCTYPE html>\n");
        htmlPageBuilder.append("<html lang=\"ru\">\n")
                .append("<head>\n")
                .append("<title>Simple HTML input form</title>\n")
                .append("<script language=\"JavaScript\" type=\"text/javascript\">\n")
                .append(generateScriptTagContent(javaScriptFunctions.keySet()))
                .append("</script>\n")
                .append("</head>\n")
                .append("<body>\n")
                .append(generateBodyTagContent(htmlTags, javaScriptFunctions))
                .append("</body>\n")
                .append("</html>");
        return Jsoup.parse(htmlPageBuilder.toString());
    }

    private Map<JavaScriptFunction, String> extractJSFunctions(List<HtmlTag> htmlTags) {
        Map<JavaScriptFunction, String> javaScriptFunctions = new HashMap<>();
        for (HtmlTag htmlTag : htmlTags) {
            Iterable<String> attributes = htmlTag.getHtmlAttributes().keySet();
            for (String attribute : attributes) {
                if (attribute.startsWith("on")) {
                    String functionInvocation = htmlTag.getHtmlAttributes().get(attribute);
                    String functionName = functionInvocation.split("\\(")[0];
                    int firstOpeningParenthesis = functionInvocation.indexOf("(");
                    int lastClosingParenthesis = functionInvocation.lastIndexOf(")");
                    String realParameters =
                            functionInvocation.substring(firstOpeningParenthesis + 1, lastClosingParenthesis);
                    javaScriptFunctions
                            .put(((JavaScriptFunctionRepository) getRepositoryFacade().getService().getRepository())
                                    .findJavaScriptFunctionByFunctionName(functionName), realParameters);
                }
            }
        }
        return javaScriptFunctions;
    }

    private String generateScriptTagContent(Iterable<JavaScriptFunction> javaScriptFunctions) {
        return null;
    }

    private String generateBodyTagContent(List<HtmlTag> htmlTags, Map<JavaScriptFunction, String> javaScriptFunctions) {
        return null;
    }
}
