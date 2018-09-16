package com.github.FlorianSteenbuck.other.punycode;

import com.github.FlorianSteenbuck.other.array.ArrayUtil;
import com.github.FlorianSteenbuck.other.array.CharacterSequence;
import com.github.FlorianSteenbuck.other.feature.Features;

public class Punycode extends Features<Punycode.Feature> {
    public static final int END_OF_ASCII = 128;
    protected static final Character DEFAULT_DELIMITER = '-';
    /*
     * I am using double because it is the compromise of the
     * pseudo code that explain PunnyCode and currently not defining any types
     */
    protected double base = 36;
    protected double tmin = 1;
    protected double tmax = 26;
    protected double skew = 38;
    protected double damp = 700;
    protected double initialBias = 72;
    protected double initialN = END_OF_ASCII;

    protected Character delimiter;
    protected Character[] text;

    public enum Feature {
        NEED_DELIMITER
    }

    public Punycode(String text) {
        this(text, DEFAULT_DELIMITER, new Feature[0]);
    }

    public Punycode(String text, Feature...features) {
        this(text, DEFAULT_DELIMITER, features);
    }

    public Punycode(String text, Character delimiter, Feature... features) {
        // TODO endless searching for a language that is typ safe and at the same support multiple types with good interfaces
        // Generics are not a solution to this problem
        super(features);
        char[] nativ = text.toCharArray();
        this.text = new Character[nativ.length];
        for (int i = 0; i < nativ.length; i++) {
            this.text[i] = nativ[i];
        }
        this.delimiter = delimiter;
    }

    public Punycode(String text, double...params) {
        this(text, DEFAULT_DELIMITER, new Feature[0], params);
    }

    public Punycode(String text, Character delimiter, double...params) {
        this(text, delimiter, new Feature[0], params);
    }

    public Punycode(String text, Character delimiter, Feature[] features, double...params) {
        this(text, delimiter, features);
        for (int i = 0; i < params.length && i < 7; i++) {
            switch (i) {
                // base
                case 0:
                    base = params[i];
                    break;
                // tmin
                case 1:
                    tmin = params[i];
                    break;
                // tmax
                case 2:
                    tmax = params[i];
                    break;
                // skew
                case 3:
                    skew = params[i];
                    break;
                // damp
                case 4:
                    damp = params[i];
                    break;
                // initialBias
                case 5:
                    initialBias = params[i];
                    break;
                // initialN
                case 6:
                    initialN = params[i];
                    break;
            }
        }
        if (!(0 <= tmin && tmin <= tmax && tmax <= base-1)) {
            throw new IllegalArgumentException("0 <= tmin <= tmax <= base-1 fail!");
        }
        if (!(skew >= 1)) {
            throw new IllegalArgumentException("skew >= 1 fail!");
        }
        if (!(damp >= 2)) {
            throw new IllegalArgumentException("damp >= 2 fail!");
        }
        if (!(initialBias % base <= base - tmin)) {
            throw new IllegalArgumentException("initialBias mod base <= base - tmin fail!");
        }
    }

    protected int char2Point(char ch) {
        return Character.codePointAt(new char[]{ch}, 0);
    }

    protected char point2Char(double cp) {
        return Character.toChars(intVal(cp))[0];
    }

    protected int intVal(double val) {
        return new Double(val).intValue();
    }

    protected int intVal(boolean val) {
        return val ? 1 : 0;
    }

    protected double adapt(double delta, double numPoints, boolean firstTime) {
        if (firstTime) {
            delta = Math.floor(delta / damp);
        } else {
            delta = delta / 2;
        }
        delta += Math.floor(delta / numPoints);
        double k = 0;

        while (delta > ((base - tmin) * tmax) / 2) {
            delta = Math.floor(delta / (base - tmin));
            k += base;
        }
        return Math.floor(k + (base - tmin + 1) * delta / (delta + skew));
    }

    protected double digitToBasic(double digit, double flag) {
        return digit + 22 + 75 * (intVal(digit < 26)) - ((intVal(flag != 0)) << 5);
    }

    protected double basicToDigit(double codePoint) {
        if (codePoint - 0x30 < 0x0A) {
            return codePoint - 0x16;
        }
        if (codePoint - 0x41 < 0x1A) {
            return codePoint - 0x41;
        }
        if (codePoint - 0x61 < 0x1A) {
            return codePoint - 0x61;
        }
        return base;
    }

    public String decode() {
        CharacterSequence output = new CharacterSequence();
        int textLength = text.length;

        double n = initialN;
        double i = 0;
        double bias = initialBias;

        int basic = ArrayUtil.lastIndexOf(text, delimiter);
        if (basic < 0) {
            basic = 0;
        }

        for (int j = 0; j < basic; j++) {
            char mayBasic = text[j];
            output.push(mayBasic);
        }

        for (int index = basic > 0 ? basic + 1 : 0; index < textLength;) {
            double oldi = i;
            double w = 1;
            for (double k = base; ; k+=base) {
                double digit = basicToDigit(char2Point(text[index]));
                index++;
                i += digit * w;

                double t;
                if (k <= bias) {
                    t = tmin;
                } else if (k >= bias + tmax) {
                    t = tmax;
                } else {
                    t = k - bias;
                }

                if (digit < t) {
                    break;
                }

                w *= base - t;
            }
            int numPoints = output.length() + 1;
            bias = adapt(i - oldi, numPoints, (oldi == 0));
            n += Math.floor(i / numPoints);
            i %= numPoints;
            output.splice(intVal(i), 0, point2Char(n));
            i++;
        }
        return output.toString();
    }

    public String encode() {
        CharacterSequence output = new CharacterSequence();
        int textLength = text.length;
        boolean needDelimiter = got(Feature.NEED_DELIMITER);

        double n = initialN;
        double delta = 0;
        double bias = initialBias;

        for (char ch:text) {
            if (char2Point(ch) < END_OF_ASCII) {
                output.push(ch);
            }
        }

        int basicLength = output.length();
        int handledCPCount = basicLength;

        if (basicLength > 0 || needDelimiter) {
            output.push(delimiter);
        }

        while (handledCPCount < textLength) {
            double m = intVal(Double.MAX_VALUE);
            for (char currentValue:text) {
                if (char2Point(currentValue) >= n && char2Point(currentValue) < m) {
                    m = currentValue;
                }
            }

            double handledCPCountPlusOne = handledCPCount + 1;
            delta += (m - n) * handledCPCountPlusOne;
            n = m;
            for (char currentValue:text) {
                if (currentValue == n) {
                    double q = delta;
                    for (double k = base; ; k += base) {
                        double t;
                        if (k <= bias) {
                            t = tmin;
                        } else if (k >= bias + tmax) {
                            t = tmax;
                        } else {
                            t = k - bias;
                        }

                        if (q < t) {
                            break;
                        }

                        double qMinusT = q - t;
                        double baseMinusT = base - t;

                        double digit = t + qMinusT % baseMinusT;
                        output.push(point2Char(digitToBasic(digit, 0)));
                        q = Math.floor(qMinusT / baseMinusT);
                    }
                    output.push(point2Char(digitToBasic(q, 0)));
                    bias = adapt(delta, handledCPCountPlusOne, handledCPCount == basicLength);
                    delta = 0;
                    handledCPCount++;
                }
            }

            delta++;
            n++;
        }

        if (output.length() == (basicLength+1) && (!needDelimiter)) {
            output = output.subSequence(0, output.length()-1);
        }

        return output.toString();
    }
}
