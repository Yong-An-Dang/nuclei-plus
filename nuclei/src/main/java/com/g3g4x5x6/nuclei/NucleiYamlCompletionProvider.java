package com.g3g4x5x6.nuclei;

import lombok.extern.slf4j.Slf4j;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;

@Slf4j
public class NucleiYamlCompletionProvider extends DefaultCompletionProvider {

    public NucleiYamlCompletionProvider() {
        this.setAutoActivationRules(true, "abcdefghijklmnopqrstuvwxyz.");
        initKeyWord();
        initShortHand();
        log.debug("Load BashCompletionProvider");
    }

    private void initKeyWord() {
        // Add completions for all Bash keywords. A BasicCompletion is just
        // a straightforward word completion.
        this.addCompletion(new BasicCompletion(this, "id: \"\""));

        this.addCompletion(new BasicCompletion(this, "info:"));
        this.addCompletion(new BasicCompletion(this, "name: "));
        this.addCompletion(new BasicCompletion(this, "author: "));
        this.addCompletion(new BasicCompletion(this, "severity: "));
        this.addCompletion(new BasicCompletion(this, "description: "));
        this.addCompletion(new BasicCompletion(this, "reference: "));
        this.addCompletion(new BasicCompletion(this, "tags: "));

        // ... etc ...
        this.addCompletion(new BasicCompletion(this, "requests:"));
        this.addCompletion(new BasicCompletion(this, "method: "));
        this.addCompletion(new BasicCompletion(this, "path:"));
        this.addCompletion(new BasicCompletion(this, "redirects: "));
        this.addCompletion(new BasicCompletion(this, "max-redirects: "));
    }

    private void initShortHand() {
        // Add a couple of "shorthand" completions. These completions don't
        // require the input text to be the same thing as the replacement text.
        this.addCompletion(new ShorthandCompletion(this, "info",
                "info:\n  name: \n  author: \n",
                "if COMMANDS; then COMMANDS; [ elif COMMANDS; then COMMANDS; ]... >"));
        this.addCompletion(new ShorthandCompletion(this, "for",
                "for (( exp1; exp2; exp3 )); \ndo \n    COMMANDS; \ndone",
                "for (( exp1; exp2; exp3 )); \ndo \n    COMMANDS; \ndone"));
        this.addCompletion(new ShorthandCompletion(this, "while",
                "while COMMANDS; \ndo \n    COMMANDS; \ndone",
                "while COMMANDS; do COMMANDS; done"));
        this.addCompletion(new ShorthandCompletion(this, "func",
                "function name {\n    COMMANDS ; \n}",
                "function name { COMMANDS ; } or name () { COMMANDS ; }"));
    }
}
