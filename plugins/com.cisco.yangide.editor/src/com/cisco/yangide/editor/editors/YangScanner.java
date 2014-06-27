package com.cisco.yangide.editor.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.NumberRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

public class YangScanner extends AbstractYangScanner{

    private static String[] tokenProperties= {
        IYangColorConstants.YANG_COMMENT,
        IYangColorConstants.YANG_IDENTIFIER,
        IYangColorConstants.YANG_KEYWORD,
        IYangColorConstants.YANG_STRING,
        IYangColorConstants.YANG_TYPE,
        IYangColorConstants.YANG_NUMBER,
    };    
    
    static String[] keywords = {
        "yin-element", "yang-version", "when", "value", "uses", 
        "units", "unique", "typedef", "type", "submodule", "status", 
        "rpc", "revision-date", "revision", "require-instance", "refine", 
        "reference", "range", "presence", "prefix", "position", "pattern", 
        "path", "output", "organization", "ordered-by", "notification", 
        "namespace", "must", "module", "min-elements", "max-elements", 
        "mandatory", "list", "length", "leaf-list", "leaf", "key", 
        "input", "include", "import", "if-feature", "identity", "grouping", 
        "fraction-digits", "feature", "deviate", "deviation", "extension", 
        "error-message", "error-app-tag", "enum", "description", "default", 
        "container", "contact", "config", "choice", "case", "bit", 
        "belongs-to", "base", "augment", "argument", "anyxml"
    };        

    static String[] types = { "binary", "bits", "boolean", "decimal64", "empty", "enumeration", "identityref", "instance-identifier", "int8", "int16",
            "int32", "int64", "leafref", "string", "uint8", "uint16", "uint32", "uint64", "union" };
    /**
     * @param manager
     * @param store
     */
    public YangScanner(IColorManager manager, IPreferenceStore store) {
        super(manager, store);
        initialize();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.text.AbstractJavaScanner#getTokenProperties()
     */
    @Override
    protected String[] getTokenProperties() {
        return tokenProperties;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.text.AbstractJavaScanner#createRules()
     */
    @Override
    protected List<IRule> createRules() {

        List<IRule> rules= new ArrayList<IRule>();        
        // Add generic whitespace rule.
        rules.add(new WhitespaceRule(new YangWhitespaceDetector()));

//        IToken string = getToken(IYangColorConstants.YANG_STRING);
//        rules.add(new MultiLineRule("\"", "\"", string));
//        
//        rules.add(new MultiLineRule("'", "'", string)); 
        
        IToken comment = getToken(IYangColorConstants.YANG_COMMENT);
        rules.add(new MultiLineRule("/*", "*/", comment));
        
        rules.add(new EndOfLineRule("//", comment)); //$NON-NLS-1$

        
        IToken identifier = getToken(IYangColorConstants.YANG_IDENTIFIER);
        IToken keyword = getToken(IYangColorConstants.YANG_KEYWORD);
        IToken type = getToken(IYangColorConstants.YANG_TYPE);
        
        WordRule wordRule = new WordRule(new YangWordDetector(), identifier);

        for (int i = 0; i < keywords.length; i++) {
            String word = keywords[i];
            wordRule.addWord(word, keyword);
        }
        
        for (int i = 0; i < types.length; i++) {
            String word = types[i];
            wordRule.addWord(word, type);
        }        
        
        rules.add(wordRule);
        
        IToken number = getToken(IYangColorConstants.YANG_NUMBER);
        NumberRule numberRule = new NumberRule(number);
        rules.add(numberRule);

        return rules;
    }
}
