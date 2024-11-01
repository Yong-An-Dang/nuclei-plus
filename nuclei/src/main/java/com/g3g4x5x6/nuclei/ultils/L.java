package com.g3g4x5x6.nuclei.ultils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class L {
    /**
     * String pattern1 = "{0}，你好！你于  {1} 消费  {2} 元。";
     * <p>
     * Object[] params = { "Jack", new GregorianCalendar().getTime(), 8888 };
     * <p>
     * String msg1 = MessageFormat.format(pattern1, params);
     */
    public static Locale locale = Locale.getDefault();
    private static final ResourceBundle rb = ResourceBundle.getBundle("locale/resource", locale);

    public static String M(String key) {
        return new MessageFormat(rb.getString(key), locale).format(null);
    }

    /**
     * @param textForTip 用于开发提示，推荐使用
     */
    public static String M(String key, String textForTip) {
        return new MessageFormat(rb.getString(key), locale).format(null);
    }

    public static String M(String key, Object[] params) {
        return new MessageFormat(rb.getString(key), locale).format(params);
    }

    public static void main(String[] args) {
        System.out.println(L.M("bar.menu.open", "打开"));
    }
}
