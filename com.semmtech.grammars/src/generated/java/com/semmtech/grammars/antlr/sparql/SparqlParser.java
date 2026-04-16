// $ANTLR 3.2 Sep 23, 2009 12:02:23 C:\\Users\\Mike Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g 2014-02-14 15:47:54

package com.semmtech.grammars.antlr.sparql;


import org.antlr.runtime.BitSet;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.TokenStream;


public class SparqlParser extends Parser {
    public static final String[] tokenNames = new String[] { "<invalid>", "<EOR>", "<DOWN>",
            "<UP>", "BASE", "IRI_REF", "PREFIX", "PNAME_NS", "SELECT", "DISTINCT", "REDUCED",
            "ASTERISK", "CONSTRUCT", "DESCRIBE", "ASK", "FROM", "NAMED", "WHERE", "ORDER", "BY",
            "ASC", "DESC", "LIMIT", "INTEGER", "OFFSET", "OPEN_CURLY_BRACE", "DOT",
            "CLOSE_CURLY_BRACE", "OPTIONAL", "GRAPH", "UNION", "FILTER", "OPEN_BRACE",
            "CLOSE_BRACE", "COMMA", "SEMICOLON", "A", "OPEN_SQUARE_BRACE", "CLOSE_SQUARE_BRACE",
            "VAR1", "VAR2", "OR", "AND", "EQUAL", "NOT_EQUAL", "LESS", "GREATER", "LESS_EQUAL",
            "GREATER_EQUAL", "PLUS", "MINUS", "DIVIDE", "NOT", "STR", "LANG", "LANGMATCHES",
            "DATATYPE", "BOUND", "SAMETERM", "ISIRI", "ISURI", "ISBLANK", "ISLITERAL", "REGEX",
            "LANGTAG", "REFERENCE", "DECIMAL", "DOUBLE", "INTEGER_POSITIVE", "DECIMAL_POSITIVE",
            "DOUBLE_POSITIVE", "INTEGER_NEGATIVE", "DECIMAL_NEGATIVE", "DOUBLE_NEGATIVE", "TRUE",
            "FALSE", "STRING_LITERAL1", "STRING_LITERAL2", "STRING_LITERAL_LONG1",
            "STRING_LITERAL_LONG2", "PNAME_LN", "BLANK_NODE_LABEL", "EOL", "WS", "PN_PREFIX",
            "PN_LOCAL", "VARNAME", "PN_CHARS_BASE", "DIGIT", "EXPONENT", "ECHAR", "PN_CHARS_U",
            "PN_CHARS", "COMMENT", "ANY" };
    public static final int PREFIX = 6;
    public static final int EXPONENT = 89;
    public static final int CLOSE_SQUARE_BRACE = 38;
    public static final int GRAPH = 29;
    public static final int REGEX = 63;
    public static final int PNAME_LN = 80;
    public static final int CONSTRUCT = 12;
    public static final int NOT = 52;
    public static final int EOF = -1;
    public static final int VARNAME = 86;
    public static final int ISLITERAL = 62;
    public static final int GREATER = 46;
    public static final int EOL = 82;
    public static final int NOT_EQUAL = 44;
    public static final int LESS = 45;
    public static final int LANGMATCHES = 55;
    public static final int DOUBLE = 67;
    public static final int BASE = 4;
    public static final int PN_CHARS_U = 91;
    public static final int COMMENT = 93;
    public static final int OPEN_CURLY_BRACE = 25;
    public static final int SELECT = 8;
    public static final int CLOSE_CURLY_BRACE = 27;
    public static final int DOUBLE_POSITIVE = 70;
    public static final int DIVIDE = 51;
    public static final int BOUND = 57;
    public static final int ISIRI = 59;
    public static final int A = 36;
    public static final int ASC = 20;
    public static final int ASK = 14;
    public static final int BLANK_NODE_LABEL = 81;
    public static final int SEMICOLON = 35;
    public static final int ISBLANK = 61;
    public static final int WS = 83;
    public static final int NAMED = 16;
    public static final int INTEGER_POSITIVE = 68;
    public static final int OR = 41;
    public static final int STRING_LITERAL2 = 77;
    public static final int FILTER = 31;
    public static final int DESCRIBE = 13;
    public static final int STRING_LITERAL1 = 76;
    public static final int PN_CHARS = 92;
    public static final int DATATYPE = 56;
    public static final int LESS_EQUAL = 47;
    public static final int DOUBLE_NEGATIVE = 73;
    public static final int FROM = 15;
    public static final int FALSE = 75;
    public static final int DISTINCT = 9;
    public static final int LANG = 54;
    public static final int IRI_REF = 5;
    public static final int WHERE = 17;
    public static final int ORDER = 18;
    public static final int LIMIT = 22;
    public static final int AND = 42;
    public static final int ASTERISK = 11;
    public static final int ISURI = 60;
    public static final int STR = 53;
    public static final int SAMETERM = 58;
    public static final int COMMA = 34;
    public static final int OFFSET = 24;
    public static final int EQUAL = 43;
    public static final int DECIMAL_POSITIVE = 69;
    public static final int PLUS = 49;
    public static final int DIGIT = 88;
    public static final int DOT = 26;
    public static final int INTEGER = 23;
    public static final int BY = 19;
    public static final int REDUCED = 10;
    public static final int INTEGER_NEGATIVE = 71;
    public static final int PN_LOCAL = 85;
    public static final int PNAME_NS = 7;
    public static final int REFERENCE = 65;
    public static final int CLOSE_BRACE = 33;
    public static final int MINUS = 50;
    public static final int TRUE = 74;
    public static final int OPEN_SQUARE_BRACE = 37;
    public static final int UNION = 30;
    public static final int ECHAR = 90;
    public static final int OPTIONAL = 28;
    public static final int ANY = 94;
    public static final int STRING_LITERAL_LONG2 = 79;
    public static final int PN_CHARS_BASE = 87;
    public static final int DECIMAL = 66;
    public static final int VAR1 = 39;
    public static final int VAR2 = 40;
    public static final int STRING_LITERAL_LONG1 = 78;
    public static final int DECIMAL_NEGATIVE = 72;
    public static final int PN_PREFIX = 84;
    public static final int DESC = 21;
    public static final int OPEN_BRACE = 32;
    public static final int GREATER_EQUAL = 48;
    public static final int LANGTAG = 64;

    // delegates
    // delegators

    public SparqlParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }

    public SparqlParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);

    }

    @Override
    public String[] getTokenNames() {
        return SparqlParser.tokenNames;
    }

    @Override
    public String getGrammarFileName() {
        return "C:\\Users\\Mike Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g";
    }

    // $ANTLR start "query"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:71:1:
    // query : prologue ( selectQuery | constructQuery | describeQuery |
    // askQuery ) EOF ;
    public final void query() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:72:5:
            // ( prologue ( selectQuery | constructQuery | describeQuery |
            // askQuery ) EOF )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:72:7:
            // prologue ( selectQuery | constructQuery | describeQuery |
            // askQuery ) EOF
            {
                pushFollow(FOLLOW_prologue_in_query36);
                prologue();

                state._fsp--;

                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:72:16:
                // ( selectQuery | constructQuery | describeQuery | askQuery )
                int alt1 = 4;
                switch (input.LA(1)) {
                case SELECT: {
                    alt1 = 1;
                }
                    break;
                case CONSTRUCT: {
                    alt1 = 2;
                }
                    break;
                case DESCRIBE: {
                    alt1 = 3;
                }
                    break;
                case ASK: {
                    alt1 = 4;
                }
                    break;
                default:
                    NoViableAltException nvae = new NoViableAltException("", 1, 0, input);

                    throw nvae;
                }

                switch (alt1) {
                case 1:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:72:18:
                // selectQuery
                {
                    pushFollow(FOLLOW_selectQuery_in_query40);
                    selectQuery();

                    state._fsp--;

                }
                    break;
                case 2:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:72:32:
                // constructQuery
                {
                    pushFollow(FOLLOW_constructQuery_in_query44);
                    constructQuery();

                    state._fsp--;

                }
                    break;
                case 3:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:72:49:
                // describeQuery
                {
                    pushFollow(FOLLOW_describeQuery_in_query48);
                    describeQuery();

                    state._fsp--;

                }
                    break;
                case 4:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:72:65:
                // askQuery
                {
                    pushFollow(FOLLOW_askQuery_in_query52);
                    askQuery();

                    state._fsp--;

                }
                    break;

                }

                match(input, EOF, FOLLOW_EOF_in_query56);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "query"

    // $ANTLR start "prologue"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:75:1:
    // prologue : ( baseDecl )? ( prefixDecl )* ;
    public final void prologue() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:76:5:
            // ( ( baseDecl )? ( prefixDecl )* )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:76:7:
            // ( baseDecl )? ( prefixDecl )*
            {
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:76:7:
                // ( baseDecl )?
                int alt2 = 2;
                int LA2_0 = input.LA(1);

                if ((LA2_0 == BASE)) {
                    alt2 = 1;
                }
                switch (alt2) {
                case 1:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:76:7:
                // baseDecl
                {
                    pushFollow(FOLLOW_baseDecl_in_prologue73);
                    baseDecl();

                    state._fsp--;

                }
                    break;

                }

                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:76:17:
                // ( prefixDecl )*
                loop3: do {
                    int alt3 = 2;
                    int LA3_0 = input.LA(1);

                    if ((LA3_0 == PREFIX)) {
                        alt3 = 1;
                    }

                    switch (alt3) {
                    case 1:
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:76:17:
                    // prefixDecl
                    {
                        pushFollow(FOLLOW_prefixDecl_in_prologue76);
                        prefixDecl();

                        state._fsp--;

                    }
                        break;

                    default:
                        break loop3;
                    }
                } while (true);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "prologue"

    // $ANTLR start "baseDecl"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:80:1:
    // baseDecl : BASE IRI_REF ;
    public final void baseDecl() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:81:5:
            // ( BASE IRI_REF )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:81:7:
            // BASE IRI_REF
            {
                match(input, BASE, FOLLOW_BASE_in_baseDecl95);
                match(input, IRI_REF, FOLLOW_IRI_REF_in_baseDecl97);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "baseDecl"

    // $ANTLR start "prefixDecl"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:84:1:
    // prefixDecl : PREFIX PNAME_NS IRI_REF ;
    public final void prefixDecl() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:85:5:
            // ( PREFIX PNAME_NS IRI_REF )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:85:7:
            // PREFIX PNAME_NS IRI_REF
            {
                match(input, PREFIX, FOLLOW_PREFIX_in_prefixDecl114);
                match(input, PNAME_NS, FOLLOW_PNAME_NS_in_prefixDecl116);
                match(input, IRI_REF, FOLLOW_IRI_REF_in_prefixDecl118);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "prefixDecl"

    // $ANTLR start "selectQuery"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:88:1:
    // selectQuery : SELECT ( DISTINCT | REDUCED )? ( ( var )+ | ASTERISK ) (
    // datasetClause )* whereClause solutionModifier ;
    public final void selectQuery() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:89:5:
            // ( SELECT ( DISTINCT | REDUCED )? ( ( var )+ | ASTERISK ) (
            // datasetClause )* whereClause solutionModifier )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:89:7:
            // SELECT ( DISTINCT | REDUCED )? ( ( var )+ | ASTERISK ) (
            // datasetClause )* whereClause solutionModifier
            {
                match(input, SELECT, FOLLOW_SELECT_in_selectQuery135);
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:89:14:
                // ( DISTINCT | REDUCED )?
                int alt4 = 2;
                int LA4_0 = input.LA(1);

                if (((LA4_0 >= DISTINCT && LA4_0 <= REDUCED))) {
                    alt4 = 1;
                }
                switch (alt4) {
                case 1:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:
                {
                    if ((input.LA(1) >= DISTINCT && input.LA(1) <= REDUCED)) {
                        input.consume();
                        state.errorRecovery = false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null, input);
                        throw mse;
                    }

                }
                    break;

                }

                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:89:38:
                // ( ( var )+ | ASTERISK )
                int alt6 = 2;
                int LA6_0 = input.LA(1);

                if (((LA6_0 >= VAR1 && LA6_0 <= VAR2))) {
                    alt6 = 1;
                }
                else if ((LA6_0 == ASTERISK)) {
                    alt6 = 2;
                }
                else {
                    NoViableAltException nvae = new NoViableAltException("", 6, 0, input);

                    throw nvae;
                }
                switch (alt6) {
                case 1:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:89:40:
                // ( var )+
                {
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:89:40:
                    // ( var )+
                    int cnt5 = 0;
                    loop5: do {
                        int alt5 = 2;
                        int LA5_0 = input.LA(1);

                        if (((LA5_0 >= VAR1 && LA5_0 <= VAR2))) {
                            alt5 = 1;
                        }

                        switch (alt5) {
                        case 1:
                        // C:\\Users\\Mike
                        // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:89:40:
                        // var
                        {
                            pushFollow(FOLLOW_var_in_selectQuery150);
                            var();

                            state._fsp--;

                        }
                            break;

                        default:
                            if (cnt5 >= 1) {
                                break loop5;
                            }
                            EarlyExitException eee = new EarlyExitException(5, input);
                            throw eee;
                        }
                        cnt5++;
                    } while (true);

                }
                    break;
                case 2:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:89:47:
                // ASTERISK
                {
                    match(input, ASTERISK, FOLLOW_ASTERISK_in_selectQuery155);

                }
                    break;

                }

                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:89:58:
                // ( datasetClause )*
                loop7: do {
                    int alt7 = 2;
                    int LA7_0 = input.LA(1);

                    if ((LA7_0 == FROM)) {
                        alt7 = 1;
                    }

                    switch (alt7) {
                    case 1:
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:89:58:
                    // datasetClause
                    {
                        pushFollow(FOLLOW_datasetClause_in_selectQuery159);
                        datasetClause();

                        state._fsp--;

                    }
                        break;

                    default:
                        break loop7;
                    }
                } while (true);

                pushFollow(FOLLOW_whereClause_in_selectQuery162);
                whereClause();

                state._fsp--;

                pushFollow(FOLLOW_solutionModifier_in_selectQuery164);
                solutionModifier();

                state._fsp--;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "selectQuery"

    // $ANTLR start "constructQuery"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:92:1:
    // constructQuery : CONSTRUCT constructTemplate ( datasetClause )*
    // whereClause solutionModifier ;
    public final void constructQuery() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:93:5:
            // ( CONSTRUCT constructTemplate ( datasetClause )* whereClause
            // solutionModifier )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:93:7:
            // CONSTRUCT constructTemplate ( datasetClause )* whereClause
            // solutionModifier
            {
                match(input, CONSTRUCT, FOLLOW_CONSTRUCT_in_constructQuery181);
                pushFollow(FOLLOW_constructTemplate_in_constructQuery183);
                constructTemplate();

                state._fsp--;

                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:93:35:
                // ( datasetClause )*
                loop8: do {
                    int alt8 = 2;
                    int LA8_0 = input.LA(1);

                    if ((LA8_0 == FROM)) {
                        alt8 = 1;
                    }

                    switch (alt8) {
                    case 1:
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:93:35:
                    // datasetClause
                    {
                        pushFollow(FOLLOW_datasetClause_in_constructQuery185);
                        datasetClause();

                        state._fsp--;

                    }
                        break;

                    default:
                        break loop8;
                    }
                } while (true);

                pushFollow(FOLLOW_whereClause_in_constructQuery188);
                whereClause();

                state._fsp--;

                pushFollow(FOLLOW_solutionModifier_in_constructQuery190);
                solutionModifier();

                state._fsp--;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "constructQuery"

    // $ANTLR start "describeQuery"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:96:1:
    // describeQuery : DESCRIBE ( ( varOrIRIref )+ | ASTERISK ) ( datasetClause
    // )* ( whereClause )? solutionModifier ;
    public final void describeQuery() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:97:5:
            // ( DESCRIBE ( ( varOrIRIref )+ | ASTERISK ) ( datasetClause )* (
            // whereClause )? solutionModifier )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:97:7:
            // DESCRIBE ( ( varOrIRIref )+ | ASTERISK ) ( datasetClause )* (
            // whereClause )? solutionModifier
            {
                match(input, DESCRIBE, FOLLOW_DESCRIBE_in_describeQuery207);
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:97:16:
                // ( ( varOrIRIref )+ | ASTERISK )
                int alt10 = 2;
                int LA10_0 = input.LA(1);

                if ((LA10_0 == IRI_REF || LA10_0 == PNAME_NS || (LA10_0 >= VAR1 && LA10_0 <= VAR2) || LA10_0 == PNAME_LN)) {
                    alt10 = 1;
                }
                else if ((LA10_0 == ASTERISK)) {
                    alt10 = 2;
                }
                else {
                    NoViableAltException nvae = new NoViableAltException("", 10, 0, input);

                    throw nvae;
                }
                switch (alt10) {
                case 1:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:97:18:
                // ( varOrIRIref )+
                {
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:97:18:
                    // ( varOrIRIref )+
                    int cnt9 = 0;
                    loop9: do {
                        int alt9 = 2;
                        int LA9_0 = input.LA(1);

                        if ((LA9_0 == IRI_REF || LA9_0 == PNAME_NS
                                || (LA9_0 >= VAR1 && LA9_0 <= VAR2) || LA9_0 == PNAME_LN)) {
                            alt9 = 1;
                        }

                        switch (alt9) {
                        case 1:
                        // C:\\Users\\Mike
                        // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:97:18:
                        // varOrIRIref
                        {
                            pushFollow(FOLLOW_varOrIRIref_in_describeQuery211);
                            varOrIRIref();

                            state._fsp--;

                        }
                            break;

                        default:
                            if (cnt9 >= 1) {
                                break loop9;
                            }
                            EarlyExitException eee = new EarlyExitException(9, input);
                            throw eee;
                        }
                        cnt9++;
                    } while (true);

                }
                    break;
                case 2:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:97:33:
                // ASTERISK
                {
                    match(input, ASTERISK, FOLLOW_ASTERISK_in_describeQuery216);

                }
                    break;

                }

                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:97:44:
                // ( datasetClause )*
                loop11: do {
                    int alt11 = 2;
                    int LA11_0 = input.LA(1);

                    if ((LA11_0 == FROM)) {
                        alt11 = 1;
                    }

                    switch (alt11) {
                    case 1:
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:97:44:
                    // datasetClause
                    {
                        pushFollow(FOLLOW_datasetClause_in_describeQuery220);
                        datasetClause();

                        state._fsp--;

                    }
                        break;

                    default:
                        break loop11;
                    }
                } while (true);

                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:97:59:
                // ( whereClause )?
                int alt12 = 2;
                int LA12_0 = input.LA(1);

                if ((LA12_0 == WHERE || LA12_0 == OPEN_CURLY_BRACE)) {
                    alt12 = 1;
                }
                switch (alt12) {
                case 1:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:97:59:
                // whereClause
                {
                    pushFollow(FOLLOW_whereClause_in_describeQuery223);
                    whereClause();

                    state._fsp--;

                }
                    break;

                }

                pushFollow(FOLLOW_solutionModifier_in_describeQuery226);
                solutionModifier();

                state._fsp--;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "describeQuery"

    // $ANTLR start "askQuery"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:100:1:
    // askQuery : ASK ( datasetClause )* whereClause ;
    public final void askQuery() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:101:5:
            // ( ASK ( datasetClause )* whereClause )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:101:7:
            // ASK ( datasetClause )* whereClause
            {
                match(input, ASK, FOLLOW_ASK_in_askQuery243);
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:101:11:
                // ( datasetClause )*
                loop13: do {
                    int alt13 = 2;
                    int LA13_0 = input.LA(1);

                    if ((LA13_0 == FROM)) {
                        alt13 = 1;
                    }

                    switch (alt13) {
                    case 1:
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:101:11:
                    // datasetClause
                    {
                        pushFollow(FOLLOW_datasetClause_in_askQuery245);
                        datasetClause();

                        state._fsp--;

                    }
                        break;

                    default:
                        break loop13;
                    }
                } while (true);

                pushFollow(FOLLOW_whereClause_in_askQuery248);
                whereClause();

                state._fsp--;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "askQuery"

    // $ANTLR start "datasetClause"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:104:1:
    // datasetClause : FROM ( defaultGraphClause | namedGraphClause ) ;
    public final void datasetClause() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:105:5:
            // ( FROM ( defaultGraphClause | namedGraphClause ) )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:105:7:
            // FROM ( defaultGraphClause | namedGraphClause )
            {
                match(input, FROM, FOLLOW_FROM_in_datasetClause265);
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:105:12:
                // ( defaultGraphClause | namedGraphClause )
                int alt14 = 2;
                int LA14_0 = input.LA(1);

                if ((LA14_0 == IRI_REF || LA14_0 == PNAME_NS || LA14_0 == PNAME_LN)) {
                    alt14 = 1;
                }
                else if ((LA14_0 == NAMED)) {
                    alt14 = 2;
                }
                else {
                    NoViableAltException nvae = new NoViableAltException("", 14, 0, input);

                    throw nvae;
                }
                switch (alt14) {
                case 1:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:105:14:
                // defaultGraphClause
                {
                    pushFollow(FOLLOW_defaultGraphClause_in_datasetClause269);
                    defaultGraphClause();

                    state._fsp--;

                }
                    break;
                case 2:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:105:35:
                // namedGraphClause
                {
                    pushFollow(FOLLOW_namedGraphClause_in_datasetClause273);
                    namedGraphClause();

                    state._fsp--;

                }
                    break;

                }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "datasetClause"

    // $ANTLR start "defaultGraphClause"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:108:1:
    // defaultGraphClause : sourceSelector ;
    public final void defaultGraphClause() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:109:5:
            // ( sourceSelector )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:109:7:
            // sourceSelector
            {
                pushFollow(FOLLOW_sourceSelector_in_defaultGraphClause292);
                sourceSelector();

                state._fsp--;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "defaultGraphClause"

    // $ANTLR start "namedGraphClause"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:112:1:
    // namedGraphClause : NAMED sourceSelector ;
    public final void namedGraphClause() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:113:5:
            // ( NAMED sourceSelector )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:113:7:
            // NAMED sourceSelector
            {
                match(input, NAMED, FOLLOW_NAMED_in_namedGraphClause309);
                pushFollow(FOLLOW_sourceSelector_in_namedGraphClause311);
                sourceSelector();

                state._fsp--;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "namedGraphClause"

    // $ANTLR start "sourceSelector"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:116:1:
    // sourceSelector : iriRef ;
    public final void sourceSelector() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:117:5:
            // ( iriRef )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:117:7:
            // iriRef
            {
                pushFollow(FOLLOW_iriRef_in_sourceSelector328);
                iriRef();

                state._fsp--;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "sourceSelector"

    // $ANTLR start "whereClause"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:120:1:
    // whereClause : ( WHERE )? groupGraphPattern ;
    public final void whereClause() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:121:5:
            // ( ( WHERE )? groupGraphPattern )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:121:7:
            // ( WHERE )? groupGraphPattern
            {
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:121:7:
                // ( WHERE )?
                int alt15 = 2;
                int LA15_0 = input.LA(1);

                if ((LA15_0 == WHERE)) {
                    alt15 = 1;
                }
                switch (alt15) {
                case 1:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:121:7:
                // WHERE
                {
                    match(input, WHERE, FOLLOW_WHERE_in_whereClause345);

                }
                    break;

                }

                pushFollow(FOLLOW_groupGraphPattern_in_whereClause348);
                groupGraphPattern();

                state._fsp--;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "whereClause"

    // $ANTLR start "solutionModifier"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:124:1:
    // solutionModifier : ( orderClause )? ( limitOffsetClauses )? ;
    public final void solutionModifier() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:125:5:
            // ( ( orderClause )? ( limitOffsetClauses )? )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:125:7:
            // ( orderClause )? ( limitOffsetClauses )?
            {
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:125:7:
                // ( orderClause )?
                int alt16 = 2;
                int LA16_0 = input.LA(1);

                if ((LA16_0 == ORDER)) {
                    alt16 = 1;
                }
                switch (alt16) {
                case 1:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:125:7:
                // orderClause
                {
                    pushFollow(FOLLOW_orderClause_in_solutionModifier365);
                    orderClause();

                    state._fsp--;

                }
                    break;

                }

                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:125:20:
                // ( limitOffsetClauses )?
                int alt17 = 2;
                int LA17_0 = input.LA(1);

                if ((LA17_0 == LIMIT || LA17_0 == OFFSET)) {
                    alt17 = 1;
                }
                switch (alt17) {
                case 1:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:125:20:
                // limitOffsetClauses
                {
                    pushFollow(FOLLOW_limitOffsetClauses_in_solutionModifier368);
                    limitOffsetClauses();

                    state._fsp--;

                }
                    break;

                }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "solutionModifier"

    // $ANTLR start "limitOffsetClauses"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:128:1:
    // limitOffsetClauses : ( limitClause ( offsetClause )? | offsetClause (
    // limitClause )? ) ;
    public final void limitOffsetClauses() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:129:5:
            // ( ( limitClause ( offsetClause )? | offsetClause ( limitClause )?
            // ) )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:129:7:
            // ( limitClause ( offsetClause )? | offsetClause ( limitClause )? )
            {
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:129:7:
                // ( limitClause ( offsetClause )? | offsetClause ( limitClause
                // )? )
                int alt20 = 2;
                int LA20_0 = input.LA(1);

                if ((LA20_0 == LIMIT)) {
                    alt20 = 1;
                }
                else if ((LA20_0 == OFFSET)) {
                    alt20 = 2;
                }
                else {
                    NoViableAltException nvae = new NoViableAltException("", 20, 0, input);

                    throw nvae;
                }
                switch (alt20) {
                case 1:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:129:9:
                // limitClause ( offsetClause )?
                {
                    pushFollow(FOLLOW_limitClause_in_limitOffsetClauses388);
                    limitClause();

                    state._fsp--;

                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:129:21:
                    // ( offsetClause )?
                    int alt18 = 2;
                    int LA18_0 = input.LA(1);

                    if ((LA18_0 == OFFSET)) {
                        alt18 = 1;
                    }
                    switch (alt18) {
                    case 1:
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:129:21:
                    // offsetClause
                    {
                        pushFollow(FOLLOW_offsetClause_in_limitOffsetClauses390);
                        offsetClause();

                        state._fsp--;

                    }
                        break;

                    }

                }
                    break;
                case 2:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:129:37:
                // offsetClause ( limitClause )?
                {
                    pushFollow(FOLLOW_offsetClause_in_limitOffsetClauses395);
                    offsetClause();

                    state._fsp--;

                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:129:50:
                    // ( limitClause )?
                    int alt19 = 2;
                    int LA19_0 = input.LA(1);

                    if ((LA19_0 == LIMIT)) {
                        alt19 = 1;
                    }
                    switch (alt19) {
                    case 1:
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:129:50:
                    // limitClause
                    {
                        pushFollow(FOLLOW_limitClause_in_limitOffsetClauses397);
                        limitClause();

                        state._fsp--;

                    }
                        break;

                    }

                }
                    break;

                }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "limitOffsetClauses"

    // $ANTLR start "orderClause"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:132:1:
    // orderClause : ORDER BY ( orderCondition )+ ;
    public final void orderClause() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:133:5:
            // ( ORDER BY ( orderCondition )+ )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:133:7:
            // ORDER BY ( orderCondition )+
            {
                match(input, ORDER, FOLLOW_ORDER_in_orderClause417);
                match(input, BY, FOLLOW_BY_in_orderClause419);
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:133:16:
                // ( orderCondition )+
                int cnt21 = 0;
                loop21: do {
                    int alt21 = 2;
                    int LA21_0 = input.LA(1);

                    if ((LA21_0 == IRI_REF || LA21_0 == PNAME_NS
                            || (LA21_0 >= ASC && LA21_0 <= DESC) || LA21_0 == OPEN_BRACE
                            || (LA21_0 >= VAR1 && LA21_0 <= VAR2)
                            || (LA21_0 >= STR && LA21_0 <= REGEX) || LA21_0 == PNAME_LN)) {
                        alt21 = 1;
                    }

                    switch (alt21) {
                    case 1:
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:133:16:
                    // orderCondition
                    {
                        pushFollow(FOLLOW_orderCondition_in_orderClause421);
                        orderCondition();

                        state._fsp--;

                    }
                        break;

                    default:
                        if (cnt21 >= 1) {
                            break loop21;
                        }
                        EarlyExitException eee = new EarlyExitException(21, input);
                        throw eee;
                    }
                    cnt21++;
                } while (true);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "orderClause"

    // $ANTLR start "orderCondition"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:136:1:
    // orderCondition : ( ( ( ASC | DESC ) brackettedExpression ) | ( constraint
    // | var ) );
    public final void orderCondition() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:137:5:
            // ( ( ( ASC | DESC ) brackettedExpression ) | ( constraint | var )
            // )
            int alt23 = 2;
            int LA23_0 = input.LA(1);

            if (((LA23_0 >= ASC && LA23_0 <= DESC))) {
                alt23 = 1;
            }
            else if ((LA23_0 == IRI_REF || LA23_0 == PNAME_NS || LA23_0 == OPEN_BRACE
                    || (LA23_0 >= VAR1 && LA23_0 <= VAR2) || (LA23_0 >= STR && LA23_0 <= REGEX) || LA23_0 == PNAME_LN)) {
                alt23 = 2;
            }
            else {
                NoViableAltException nvae = new NoViableAltException("", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
            case 1:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:137:7:
            // ( ( ASC | DESC ) brackettedExpression )
            {
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:137:7:
                // ( ( ASC | DESC ) brackettedExpression )
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:137:9:
                // ( ASC | DESC ) brackettedExpression
                {
                    if ((input.LA(1) >= ASC && input.LA(1) <= DESC)) {
                        input.consume();
                        state.errorRecovery = false;
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null, input);
                        throw mse;
                    }

                    pushFollow(FOLLOW_brackettedExpression_in_orderCondition451);
                    brackettedExpression();

                    state._fsp--;

                }

            }
                break;
            case 2:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:138:7:
            // ( constraint | var )
            {
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:138:7:
                // ( constraint | var )
                int alt22 = 2;
                int LA22_0 = input.LA(1);

                if ((LA22_0 == IRI_REF || LA22_0 == PNAME_NS || LA22_0 == OPEN_BRACE
                        || (LA22_0 >= STR && LA22_0 <= REGEX) || LA22_0 == PNAME_LN)) {
                    alt22 = 1;
                }
                else if (((LA22_0 >= VAR1 && LA22_0 <= VAR2))) {
                    alt22 = 2;
                }
                else {
                    NoViableAltException nvae = new NoViableAltException("", 22, 0, input);

                    throw nvae;
                }
                switch (alt22) {
                case 1:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:138:9:
                // constraint
                {
                    pushFollow(FOLLOW_constraint_in_orderCondition463);
                    constraint();

                    state._fsp--;

                }
                    break;
                case 2:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:138:22:
                // var
                {
                    pushFollow(FOLLOW_var_in_orderCondition467);
                    var();

                    state._fsp--;

                }
                    break;

                }

            }
                break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "orderCondition"

    // $ANTLR start "limitClause"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:141:1:
    // limitClause : LIMIT INTEGER ;
    public final void limitClause() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:142:5:
            // ( LIMIT INTEGER )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:142:7:
            // LIMIT INTEGER
            {
                match(input, LIMIT, FOLLOW_LIMIT_in_limitClause486);
                match(input, INTEGER, FOLLOW_INTEGER_in_limitClause488);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "limitClause"

    // $ANTLR start "offsetClause"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:145:1:
    // offsetClause : OFFSET INTEGER ;
    public final void offsetClause() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:146:5:
            // ( OFFSET INTEGER )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:146:7:
            // OFFSET INTEGER
            {
                match(input, OFFSET, FOLLOW_OFFSET_in_offsetClause505);
                match(input, INTEGER, FOLLOW_INTEGER_in_offsetClause507);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "offsetClause"

    // $ANTLR start "groupGraphPattern"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:149:1:
    // groupGraphPattern : OPEN_CURLY_BRACE ( triplesBlock )? ( (
    // graphPatternNotTriples | filter ) ( DOT )? ( triplesBlock )? )*
    // CLOSE_CURLY_BRACE ;
    public final void groupGraphPattern() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:150:5:
            // ( OPEN_CURLY_BRACE ( triplesBlock )? ( ( graphPatternNotTriples |
            // filter ) ( DOT )? ( triplesBlock )? )* CLOSE_CURLY_BRACE )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:150:7:
            // OPEN_CURLY_BRACE ( triplesBlock )? ( ( graphPatternNotTriples |
            // filter ) ( DOT )? ( triplesBlock )? )* CLOSE_CURLY_BRACE
            {
                match(input, OPEN_CURLY_BRACE, FOLLOW_OPEN_CURLY_BRACE_in_groupGraphPattern524);
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:150:24:
                // ( triplesBlock )?
                int alt24 = 2;
                int LA24_0 = input.LA(1);

                if ((LA24_0 == IRI_REF || LA24_0 == PNAME_NS || LA24_0 == INTEGER
                        || LA24_0 == OPEN_BRACE || LA24_0 == OPEN_SQUARE_BRACE
                        || (LA24_0 >= VAR1 && LA24_0 <= VAR2) || (LA24_0 >= DECIMAL && LA24_0 <= BLANK_NODE_LABEL))) {
                    alt24 = 1;
                }
                switch (alt24) {
                case 1:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:150:24:
                // triplesBlock
                {
                    pushFollow(FOLLOW_triplesBlock_in_groupGraphPattern526);
                    triplesBlock();

                    state._fsp--;

                }
                    break;

                }

                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:150:38:
                // ( ( graphPatternNotTriples | filter ) ( DOT )? ( triplesBlock
                // )? )*
                loop28: do {
                    int alt28 = 2;
                    int LA28_0 = input.LA(1);

                    if ((LA28_0 == OPEN_CURLY_BRACE || (LA28_0 >= OPTIONAL && LA28_0 <= GRAPH) || LA28_0 == FILTER)) {
                        alt28 = 1;
                    }

                    switch (alt28) {
                    case 1:
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:150:40:
                    // ( graphPatternNotTriples | filter ) ( DOT )? (
                    // triplesBlock )?
                    {
                        // C:\\Users\\Mike
                        // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:150:40:
                        // ( graphPatternNotTriples | filter )
                        int alt25 = 2;
                        int LA25_0 = input.LA(1);

                        if ((LA25_0 == OPEN_CURLY_BRACE || (LA25_0 >= OPTIONAL && LA25_0 <= GRAPH))) {
                            alt25 = 1;
                        }
                        else if ((LA25_0 == FILTER)) {
                            alt25 = 2;
                        }
                        else {
                            NoViableAltException nvae = new NoViableAltException("", 25, 0, input);

                            throw nvae;
                        }
                        switch (alt25) {
                        case 1:
                        // C:\\Users\\Mike
                        // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:150:42:
                        // graphPatternNotTriples
                        {
                            pushFollow(FOLLOW_graphPatternNotTriples_in_groupGraphPattern533);
                            graphPatternNotTriples();

                            state._fsp--;

                        }
                            break;
                        case 2:
                        // C:\\Users\\Mike
                        // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:150:67:
                        // filter
                        {
                            pushFollow(FOLLOW_filter_in_groupGraphPattern537);
                            filter();

                            state._fsp--;

                        }
                            break;

                        }

                        // C:\\Users\\Mike
                        // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:150:76:
                        // ( DOT )?
                        int alt26 = 2;
                        int LA26_0 = input.LA(1);

                        if ((LA26_0 == DOT)) {
                            alt26 = 1;
                        }
                        switch (alt26) {
                        case 1:
                        // C:\\Users\\Mike
                        // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:150:76:
                        // DOT
                        {
                            match(input, DOT, FOLLOW_DOT_in_groupGraphPattern541);

                        }
                            break;

                        }

                        // C:\\Users\\Mike
                        // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:150:81:
                        // ( triplesBlock )?
                        int alt27 = 2;
                        int LA27_0 = input.LA(1);

                        if ((LA27_0 == IRI_REF || LA27_0 == PNAME_NS || LA27_0 == INTEGER
                                || LA27_0 == OPEN_BRACE || LA27_0 == OPEN_SQUARE_BRACE
                                || (LA27_0 >= VAR1 && LA27_0 <= VAR2) || (LA27_0 >= DECIMAL && LA27_0 <= BLANK_NODE_LABEL))) {
                            alt27 = 1;
                        }
                        switch (alt27) {
                        case 1:
                        // C:\\Users\\Mike
                        // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:150:81:
                        // triplesBlock
                        {
                            pushFollow(FOLLOW_triplesBlock_in_groupGraphPattern544);
                            triplesBlock();

                            state._fsp--;

                        }
                            break;

                        }

                    }
                        break;

                    default:
                        break loop28;
                    }
                } while (true);

                match(input, CLOSE_CURLY_BRACE, FOLLOW_CLOSE_CURLY_BRACE_in_groupGraphPattern550);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "groupGraphPattern"

    // $ANTLR start "triplesBlock"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:153:1:
    // triplesBlock : triplesSameSubject ( DOT ( triplesBlock )? )? ;
    public final void triplesBlock() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:154:5:
            // ( triplesSameSubject ( DOT ( triplesBlock )? )? )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:154:7:
            // triplesSameSubject ( DOT ( triplesBlock )? )?
            {
                pushFollow(FOLLOW_triplesSameSubject_in_triplesBlock567);
                triplesSameSubject();

                state._fsp--;

                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:154:26:
                // ( DOT ( triplesBlock )? )?
                int alt30 = 2;
                int LA30_0 = input.LA(1);

                if ((LA30_0 == DOT)) {
                    alt30 = 1;
                }
                switch (alt30) {
                case 1:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:154:28:
                // DOT ( triplesBlock )?
                {
                    match(input, DOT, FOLLOW_DOT_in_triplesBlock571);
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:154:32:
                    // ( triplesBlock )?
                    int alt29 = 2;
                    int LA29_0 = input.LA(1);

                    if ((LA29_0 == IRI_REF || LA29_0 == PNAME_NS || LA29_0 == INTEGER
                            || LA29_0 == OPEN_BRACE || LA29_0 == OPEN_SQUARE_BRACE
                            || (LA29_0 >= VAR1 && LA29_0 <= VAR2) || (LA29_0 >= DECIMAL && LA29_0 <= BLANK_NODE_LABEL))) {
                        alt29 = 1;
                    }
                    switch (alt29) {
                    case 1:
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:154:32:
                    // triplesBlock
                    {
                        pushFollow(FOLLOW_triplesBlock_in_triplesBlock573);
                        triplesBlock();

                        state._fsp--;

                    }
                        break;

                    }

                }
                    break;

                }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "triplesBlock"

    // $ANTLR start "graphPatternNotTriples"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:157:1:
    // graphPatternNotTriples : ( optionalGraphPattern |
    // groupOrUnionGraphPattern | graphGraphPattern );
    public final void graphPatternNotTriples() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:158:5:
            // ( optionalGraphPattern | groupOrUnionGraphPattern |
            // graphGraphPattern )
            int alt31 = 3;
            switch (input.LA(1)) {
            case OPTIONAL: {
                alt31 = 1;
            }
                break;
            case OPEN_CURLY_BRACE: {
                alt31 = 2;
            }
                break;
            case GRAPH: {
                alt31 = 3;
            }
                break;
            default:
                NoViableAltException nvae = new NoViableAltException("", 31, 0, input);

                throw nvae;
            }

            switch (alt31) {
            case 1:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:158:7:
            // optionalGraphPattern
            {
                pushFollow(FOLLOW_optionalGraphPattern_in_graphPatternNotTriples594);
                optionalGraphPattern();

                state._fsp--;

            }
                break;
            case 2:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:158:30:
            // groupOrUnionGraphPattern
            {
                pushFollow(FOLLOW_groupOrUnionGraphPattern_in_graphPatternNotTriples598);
                groupOrUnionGraphPattern();

                state._fsp--;

            }
                break;
            case 3:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:158:57:
            // graphGraphPattern
            {
                pushFollow(FOLLOW_graphGraphPattern_in_graphPatternNotTriples602);
                graphGraphPattern();

                state._fsp--;

            }
                break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "graphPatternNotTriples"

    // $ANTLR start "optionalGraphPattern"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:161:1:
    // optionalGraphPattern : OPTIONAL groupGraphPattern ;
    public final void optionalGraphPattern() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:162:5:
            // ( OPTIONAL groupGraphPattern )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:162:7:
            // OPTIONAL groupGraphPattern
            {
                match(input, OPTIONAL, FOLLOW_OPTIONAL_in_optionalGraphPattern619);
                pushFollow(FOLLOW_groupGraphPattern_in_optionalGraphPattern621);
                groupGraphPattern();

                state._fsp--;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "optionalGraphPattern"

    // $ANTLR start "graphGraphPattern"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:165:1:
    // graphGraphPattern : GRAPH varOrIRIref groupGraphPattern ;
    public final void graphGraphPattern() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:166:5:
            // ( GRAPH varOrIRIref groupGraphPattern )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:166:7:
            // GRAPH varOrIRIref groupGraphPattern
            {
                match(input, GRAPH, FOLLOW_GRAPH_in_graphGraphPattern638);
                pushFollow(FOLLOW_varOrIRIref_in_graphGraphPattern640);
                varOrIRIref();

                state._fsp--;

                pushFollow(FOLLOW_groupGraphPattern_in_graphGraphPattern642);
                groupGraphPattern();

                state._fsp--;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "graphGraphPattern"

    // $ANTLR start "groupOrUnionGraphPattern"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:169:1:
    // groupOrUnionGraphPattern : groupGraphPattern ( UNION groupGraphPattern )*
    // ;
    public final void groupOrUnionGraphPattern() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:170:5:
            // ( groupGraphPattern ( UNION groupGraphPattern )* )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:170:7:
            // groupGraphPattern ( UNION groupGraphPattern )*
            {
                pushFollow(FOLLOW_groupGraphPattern_in_groupOrUnionGraphPattern659);
                groupGraphPattern();

                state._fsp--;

                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:170:25:
                // ( UNION groupGraphPattern )*
                loop32: do {
                    int alt32 = 2;
                    int LA32_0 = input.LA(1);

                    if ((LA32_0 == UNION)) {
                        alt32 = 1;
                    }

                    switch (alt32) {
                    case 1:
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:170:27:
                    // UNION groupGraphPattern
                    {
                        match(input, UNION, FOLLOW_UNION_in_groupOrUnionGraphPattern663);
                        pushFollow(FOLLOW_groupGraphPattern_in_groupOrUnionGraphPattern665);
                        groupGraphPattern();

                        state._fsp--;

                    }
                        break;

                    default:
                        break loop32;
                    }
                } while (true);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "groupOrUnionGraphPattern"

    // $ANTLR start "filter"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:173:1:
    // filter : FILTER constraint ;
    public final void filter() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:174:5:
            // ( FILTER constraint )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:174:7:
            // FILTER constraint
            {
                match(input, FILTER, FOLLOW_FILTER_in_filter685);
                pushFollow(FOLLOW_constraint_in_filter687);
                constraint();

                state._fsp--;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "filter"

    // $ANTLR start "constraint"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:177:1:
    // constraint : ( brackettedExpression | builtInCall | functionCall );
    public final void constraint() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:178:5:
            // ( brackettedExpression | builtInCall | functionCall )
            int alt33 = 3;
            switch (input.LA(1)) {
            case OPEN_BRACE: {
                alt33 = 1;
            }
                break;
            case STR:
            case LANG:
            case LANGMATCHES:
            case DATATYPE:
            case BOUND:
            case SAMETERM:
            case ISIRI:
            case ISURI:
            case ISBLANK:
            case ISLITERAL:
            case REGEX: {
                alt33 = 2;
            }
                break;
            case IRI_REF:
            case PNAME_NS:
            case PNAME_LN: {
                alt33 = 3;
            }
                break;
            default:
                NoViableAltException nvae = new NoViableAltException("", 33, 0, input);

                throw nvae;
            }

            switch (alt33) {
            case 1:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:178:7:
            // brackettedExpression
            {
                pushFollow(FOLLOW_brackettedExpression_in_constraint704);
                brackettedExpression();

                state._fsp--;

            }
                break;
            case 2:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:178:30:
            // builtInCall
            {
                pushFollow(FOLLOW_builtInCall_in_constraint708);
                builtInCall();

                state._fsp--;

            }
                break;
            case 3:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:178:44:
            // functionCall
            {
                pushFollow(FOLLOW_functionCall_in_constraint712);
                functionCall();

                state._fsp--;

            }
                break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "constraint"

    // $ANTLR start "functionCall"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:181:1:
    // functionCall : iriRef argList ;
    public final void functionCall() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:182:5:
            // ( iriRef argList )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:182:7:
            // iriRef argList
            {
                pushFollow(FOLLOW_iriRef_in_functionCall729);
                iriRef();

                state._fsp--;

                pushFollow(FOLLOW_argList_in_functionCall731);
                argList();

                state._fsp--;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "functionCall"

    // $ANTLR start "argList"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:185:1:
    // argList : ( OPEN_BRACE CLOSE_BRACE | OPEN_BRACE expression ( COMMA
    // expression )* CLOSE_BRACE ) ;
    public final void argList() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:186:5:
            // ( ( OPEN_BRACE CLOSE_BRACE | OPEN_BRACE expression ( COMMA
            // expression )* CLOSE_BRACE ) )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:186:7:
            // ( OPEN_BRACE CLOSE_BRACE | OPEN_BRACE expression ( COMMA
            // expression )* CLOSE_BRACE )
            {
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:186:7:
                // ( OPEN_BRACE CLOSE_BRACE | OPEN_BRACE expression ( COMMA
                // expression )* CLOSE_BRACE )
                int alt35 = 2;
                int LA35_0 = input.LA(1);

                if ((LA35_0 == OPEN_BRACE)) {
                    int LA35_1 = input.LA(2);

                    if ((LA35_1 == CLOSE_BRACE)) {
                        alt35 = 1;
                    }
                    else if ((LA35_1 == IRI_REF || LA35_1 == PNAME_NS || LA35_1 == INTEGER
                            || LA35_1 == OPEN_BRACE || (LA35_1 >= VAR1 && LA35_1 <= VAR2)
                            || (LA35_1 >= PLUS && LA35_1 <= MINUS)
                            || (LA35_1 >= NOT && LA35_1 <= REGEX) || (LA35_1 >= DECIMAL && LA35_1 <= PNAME_LN))) {
                        alt35 = 2;
                    }
                    else {
                        NoViableAltException nvae = new NoViableAltException("", 35, 1, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae = new NoViableAltException("", 35, 0, input);

                    throw nvae;
                }
                switch (alt35) {
                case 1:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:186:9:
                // OPEN_BRACE CLOSE_BRACE
                {
                    match(input, OPEN_BRACE, FOLLOW_OPEN_BRACE_in_argList750);
                    match(input, CLOSE_BRACE, FOLLOW_CLOSE_BRACE_in_argList752);

                }
                    break;
                case 2:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:186:34:
                // OPEN_BRACE expression ( COMMA expression )* CLOSE_BRACE
                {
                    match(input, OPEN_BRACE, FOLLOW_OPEN_BRACE_in_argList756);
                    pushFollow(FOLLOW_expression_in_argList758);
                    expression();

                    state._fsp--;

                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:186:56:
                    // ( COMMA expression )*
                    loop34: do {
                        int alt34 = 2;
                        int LA34_0 = input.LA(1);

                        if ((LA34_0 == COMMA)) {
                            alt34 = 1;
                        }

                        switch (alt34) {
                        case 1:
                        // C:\\Users\\Mike
                        // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:186:58:
                        // COMMA expression
                        {
                            match(input, COMMA, FOLLOW_COMMA_in_argList762);
                            pushFollow(FOLLOW_expression_in_argList764);
                            expression();

                            state._fsp--;

                        }
                            break;

                        default:
                            break loop34;
                        }
                    } while (true);

                    match(input, CLOSE_BRACE, FOLLOW_CLOSE_BRACE_in_argList769);

                }
                    break;

                }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "argList"

    // $ANTLR start "constructTemplate"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:189:1:
    // constructTemplate : OPEN_CURLY_BRACE ( constructTriples )?
    // CLOSE_CURLY_BRACE ;
    public final void constructTemplate() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:190:5:
            // ( OPEN_CURLY_BRACE ( constructTriples )? CLOSE_CURLY_BRACE )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:190:7:
            // OPEN_CURLY_BRACE ( constructTriples )? CLOSE_CURLY_BRACE
            {
                match(input, OPEN_CURLY_BRACE, FOLLOW_OPEN_CURLY_BRACE_in_constructTemplate788);
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:190:24:
                // ( constructTriples )?
                int alt36 = 2;
                int LA36_0 = input.LA(1);

                if ((LA36_0 == IRI_REF || LA36_0 == PNAME_NS || LA36_0 == INTEGER
                        || LA36_0 == OPEN_BRACE || LA36_0 == OPEN_SQUARE_BRACE
                        || (LA36_0 >= VAR1 && LA36_0 <= VAR2) || (LA36_0 >= DECIMAL && LA36_0 <= BLANK_NODE_LABEL))) {
                    alt36 = 1;
                }
                switch (alt36) {
                case 1:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:190:24:
                // constructTriples
                {
                    pushFollow(FOLLOW_constructTriples_in_constructTemplate790);
                    constructTriples();

                    state._fsp--;

                }
                    break;

                }

                match(input, CLOSE_CURLY_BRACE, FOLLOW_CLOSE_CURLY_BRACE_in_constructTemplate793);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "constructTemplate"

    // $ANTLR start "constructTriples"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:193:1:
    // constructTriples : triplesSameSubject ( DOT ( constructTriples )? )? ;
    public final void constructTriples() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:194:5:
            // ( triplesSameSubject ( DOT ( constructTriples )? )? )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:194:7:
            // triplesSameSubject ( DOT ( constructTriples )? )?
            {
                pushFollow(FOLLOW_triplesSameSubject_in_constructTriples810);
                triplesSameSubject();

                state._fsp--;

                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:194:26:
                // ( DOT ( constructTriples )? )?
                int alt38 = 2;
                int LA38_0 = input.LA(1);

                if ((LA38_0 == DOT)) {
                    alt38 = 1;
                }
                switch (alt38) {
                case 1:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:194:28:
                // DOT ( constructTriples )?
                {
                    match(input, DOT, FOLLOW_DOT_in_constructTriples814);
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:194:32:
                    // ( constructTriples )?
                    int alt37 = 2;
                    int LA37_0 = input.LA(1);

                    if ((LA37_0 == IRI_REF || LA37_0 == PNAME_NS || LA37_0 == INTEGER
                            || LA37_0 == OPEN_BRACE || LA37_0 == OPEN_SQUARE_BRACE
                            || (LA37_0 >= VAR1 && LA37_0 <= VAR2) || (LA37_0 >= DECIMAL && LA37_0 <= BLANK_NODE_LABEL))) {
                        alt37 = 1;
                    }
                    switch (alt37) {
                    case 1:
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:194:32:
                    // constructTriples
                    {
                        pushFollow(FOLLOW_constructTriples_in_constructTriples816);
                        constructTriples();

                        state._fsp--;

                    }
                        break;

                    }

                }
                    break;

                }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "constructTriples"

    // $ANTLR start "triplesSameSubject"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:197:1:
    // triplesSameSubject : ( varOrTerm propertyListNotEmpty | triplesNode
    // propertyList );
    public final void triplesSameSubject() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:198:5:
            // ( varOrTerm propertyListNotEmpty | triplesNode propertyList )
            int alt39 = 2;
            switch (input.LA(1)) {
            case IRI_REF:
            case PNAME_NS:
            case INTEGER:
            case VAR1:
            case VAR2:
            case DECIMAL:
            case DOUBLE:
            case INTEGER_POSITIVE:
            case DECIMAL_POSITIVE:
            case DOUBLE_POSITIVE:
            case INTEGER_NEGATIVE:
            case DECIMAL_NEGATIVE:
            case DOUBLE_NEGATIVE:
            case TRUE:
            case FALSE:
            case STRING_LITERAL1:
            case STRING_LITERAL2:
            case STRING_LITERAL_LONG1:
            case STRING_LITERAL_LONG2:
            case PNAME_LN:
            case BLANK_NODE_LABEL: {
                alt39 = 1;
            }
                break;
            case OPEN_SQUARE_BRACE: {
                int LA39_2 = input.LA(2);

                if ((LA39_2 == CLOSE_SQUARE_BRACE)) {
                    alt39 = 1;
                }
                else if ((LA39_2 == IRI_REF || LA39_2 == PNAME_NS || LA39_2 == A
                        || (LA39_2 >= VAR1 && LA39_2 <= VAR2) || LA39_2 == PNAME_LN)) {
                    alt39 = 2;
                }
                else {
                    NoViableAltException nvae = new NoViableAltException("", 39, 2, input);

                    throw nvae;
                }
            }
                break;
            case OPEN_BRACE: {
                int LA39_3 = input.LA(2);

                if ((LA39_3 == CLOSE_BRACE)) {
                    alt39 = 1;
                }
                else if ((LA39_3 == IRI_REF || LA39_3 == PNAME_NS || LA39_3 == INTEGER
                        || LA39_3 == OPEN_BRACE || LA39_3 == OPEN_SQUARE_BRACE
                        || (LA39_3 >= VAR1 && LA39_3 <= VAR2) || (LA39_3 >= DECIMAL && LA39_3 <= BLANK_NODE_LABEL))) {
                    alt39 = 2;
                }
                else {
                    NoViableAltException nvae = new NoViableAltException("", 39, 3, input);

                    throw nvae;
                }
            }
                break;
            default:
                NoViableAltException nvae = new NoViableAltException("", 39, 0, input);

                throw nvae;
            }

            switch (alt39) {
            case 1:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:198:7:
            // varOrTerm propertyListNotEmpty
            {
                pushFollow(FOLLOW_varOrTerm_in_triplesSameSubject837);
                varOrTerm();

                state._fsp--;

                pushFollow(FOLLOW_propertyListNotEmpty_in_triplesSameSubject839);
                propertyListNotEmpty();

                state._fsp--;

            }
                break;
            case 2:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:198:40:
            // triplesNode propertyList
            {
                pushFollow(FOLLOW_triplesNode_in_triplesSameSubject843);
                triplesNode();

                state._fsp--;

                pushFollow(FOLLOW_propertyList_in_triplesSameSubject845);
                propertyList();

                state._fsp--;

            }
                break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "triplesSameSubject"

    // $ANTLR start "propertyListNotEmpty"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:201:1:
    // propertyListNotEmpty : verb objectList ( SEMICOLON ( verb objectList )?
    // )* ;
    public final void propertyListNotEmpty() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:202:5:
            // ( verb objectList ( SEMICOLON ( verb objectList )? )* )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:202:7:
            // verb objectList ( SEMICOLON ( verb objectList )? )*
            {
                pushFollow(FOLLOW_verb_in_propertyListNotEmpty862);
                verb();

                state._fsp--;

                pushFollow(FOLLOW_objectList_in_propertyListNotEmpty864);
                objectList();

                state._fsp--;

                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:202:23:
                // ( SEMICOLON ( verb objectList )? )*
                loop41: do {
                    int alt41 = 2;
                    int LA41_0 = input.LA(1);

                    if ((LA41_0 == SEMICOLON)) {
                        alt41 = 1;
                    }

                    switch (alt41) {
                    case 1:
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:202:25:
                    // SEMICOLON ( verb objectList )?
                    {
                        match(input, SEMICOLON, FOLLOW_SEMICOLON_in_propertyListNotEmpty868);
                        // C:\\Users\\Mike
                        // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:202:35:
                        // ( verb objectList )?
                        int alt40 = 2;
                        int LA40_0 = input.LA(1);

                        if ((LA40_0 == IRI_REF || LA40_0 == PNAME_NS || LA40_0 == A
                                || (LA40_0 >= VAR1 && LA40_0 <= VAR2) || LA40_0 == PNAME_LN)) {
                            alt40 = 1;
                        }
                        switch (alt40) {
                        case 1:
                        // C:\\Users\\Mike
                        // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:202:37:
                        // verb objectList
                        {
                            pushFollow(FOLLOW_verb_in_propertyListNotEmpty872);
                            verb();

                            state._fsp--;

                            pushFollow(FOLLOW_objectList_in_propertyListNotEmpty874);
                            objectList();

                            state._fsp--;

                        }
                            break;

                        }

                    }
                        break;

                    default:
                        break loop41;
                    }
                } while (true);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "propertyListNotEmpty"

    // $ANTLR start "propertyList"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:205:1:
    // propertyList : ( propertyListNotEmpty )? ;
    public final void propertyList() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:206:5:
            // ( ( propertyListNotEmpty )? )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:206:7:
            // ( propertyListNotEmpty )?
            {
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:206:7:
                // ( propertyListNotEmpty )?
                int alt42 = 2;
                int LA42_0 = input.LA(1);

                if ((LA42_0 == IRI_REF || LA42_0 == PNAME_NS || LA42_0 == A
                        || (LA42_0 >= VAR1 && LA42_0 <= VAR2) || LA42_0 == PNAME_LN)) {
                    alt42 = 1;
                }
                switch (alt42) {
                case 1:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:206:7:
                // propertyListNotEmpty
                {
                    pushFollow(FOLLOW_propertyListNotEmpty_in_propertyList897);
                    propertyListNotEmpty();

                    state._fsp--;

                }
                    break;

                }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "propertyList"

    // $ANTLR start "objectList"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:209:1:
    // objectList : object ( COMMA object )* ;
    public final void objectList() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:210:5:
            // ( object ( COMMA object )* )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:210:7:
            // object ( COMMA object )*
            {
                pushFollow(FOLLOW_object_in_objectList915);
                object();

                state._fsp--;

                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:210:14:
                // ( COMMA object )*
                loop43: do {
                    int alt43 = 2;
                    int LA43_0 = input.LA(1);

                    if ((LA43_0 == COMMA)) {
                        alt43 = 1;
                    }

                    switch (alt43) {
                    case 1:
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:210:16:
                    // COMMA object
                    {
                        match(input, COMMA, FOLLOW_COMMA_in_objectList919);
                        pushFollow(FOLLOW_object_in_objectList921);
                        object();

                        state._fsp--;

                    }
                        break;

                    default:
                        break loop43;
                    }
                } while (true);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "objectList"

    // $ANTLR start "object"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:213:1:
    // object : graphNode ;
    public final void object() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:214:5:
            // ( graphNode )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:214:7:
            // graphNode
            {
                pushFollow(FOLLOW_graphNode_in_object941);
                graphNode();

                state._fsp--;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "object"

    // $ANTLR start "verb"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:217:1:
    // verb : ( varOrIRIref | A );
    public final void verb() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:218:5:
            // ( varOrIRIref | A )
            int alt44 = 2;
            int LA44_0 = input.LA(1);

            if ((LA44_0 == IRI_REF || LA44_0 == PNAME_NS || (LA44_0 >= VAR1 && LA44_0 <= VAR2) || LA44_0 == PNAME_LN)) {
                alt44 = 1;
            }
            else if ((LA44_0 == A)) {
                alt44 = 2;
            }
            else {
                NoViableAltException nvae = new NoViableAltException("", 44, 0, input);

                throw nvae;
            }
            switch (alt44) {
            case 1:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:218:7:
            // varOrIRIref
            {
                pushFollow(FOLLOW_varOrIRIref_in_verb958);
                varOrIRIref();

                state._fsp--;

            }
                break;
            case 2:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:219:7:
            // A
            {
                match(input, A, FOLLOW_A_in_verb966);

            }
                break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "verb"

    // $ANTLR start "triplesNode"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:222:1:
    // triplesNode : ( collection | blankNodePropertyList );
    public final void triplesNode() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:223:5:
            // ( collection | blankNodePropertyList )
            int alt45 = 2;
            int LA45_0 = input.LA(1);

            if ((LA45_0 == OPEN_BRACE)) {
                alt45 = 1;
            }
            else if ((LA45_0 == OPEN_SQUARE_BRACE)) {
                alt45 = 2;
            }
            else {
                NoViableAltException nvae = new NoViableAltException("", 45, 0, input);

                throw nvae;
            }
            switch (alt45) {
            case 1:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:223:7:
            // collection
            {
                pushFollow(FOLLOW_collection_in_triplesNode983);
                collection();

                state._fsp--;

            }
                break;
            case 2:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:224:7:
            // blankNodePropertyList
            {
                pushFollow(FOLLOW_blankNodePropertyList_in_triplesNode991);
                blankNodePropertyList();

                state._fsp--;

            }
                break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "triplesNode"

    // $ANTLR start "blankNodePropertyList"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:227:1:
    // blankNodePropertyList : OPEN_SQUARE_BRACE propertyListNotEmpty
    // CLOSE_SQUARE_BRACE ;
    public final void blankNodePropertyList() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:228:5:
            // ( OPEN_SQUARE_BRACE propertyListNotEmpty CLOSE_SQUARE_BRACE )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:228:7:
            // OPEN_SQUARE_BRACE propertyListNotEmpty CLOSE_SQUARE_BRACE
            {
                match(input, OPEN_SQUARE_BRACE,
                        FOLLOW_OPEN_SQUARE_BRACE_in_blankNodePropertyList1008);
                pushFollow(FOLLOW_propertyListNotEmpty_in_blankNodePropertyList1010);
                propertyListNotEmpty();

                state._fsp--;

                match(input, CLOSE_SQUARE_BRACE,
                        FOLLOW_CLOSE_SQUARE_BRACE_in_blankNodePropertyList1012);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "blankNodePropertyList"

    // $ANTLR start "collection"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:231:1:
    // collection : OPEN_BRACE ( graphNode )+ CLOSE_BRACE ;
    public final void collection() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:232:5:
            // ( OPEN_BRACE ( graphNode )+ CLOSE_BRACE )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:232:7:
            // OPEN_BRACE ( graphNode )+ CLOSE_BRACE
            {
                match(input, OPEN_BRACE, FOLLOW_OPEN_BRACE_in_collection1029);
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:232:18:
                // ( graphNode )+
                int cnt46 = 0;
                loop46: do {
                    int alt46 = 2;
                    int LA46_0 = input.LA(1);

                    if ((LA46_0 == IRI_REF || LA46_0 == PNAME_NS || LA46_0 == INTEGER
                            || LA46_0 == OPEN_BRACE || LA46_0 == OPEN_SQUARE_BRACE
                            || (LA46_0 >= VAR1 && LA46_0 <= VAR2) || (LA46_0 >= DECIMAL && LA46_0 <= BLANK_NODE_LABEL))) {
                        alt46 = 1;
                    }

                    switch (alt46) {
                    case 1:
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:232:18:
                    // graphNode
                    {
                        pushFollow(FOLLOW_graphNode_in_collection1031);
                        graphNode();

                        state._fsp--;

                    }
                        break;

                    default:
                        if (cnt46 >= 1) {
                            break loop46;
                        }
                        EarlyExitException eee = new EarlyExitException(46, input);
                        throw eee;
                    }
                    cnt46++;
                } while (true);

                match(input, CLOSE_BRACE, FOLLOW_CLOSE_BRACE_in_collection1034);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "collection"

    // $ANTLR start "graphNode"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:235:1:
    // graphNode : ( varOrTerm | triplesNode );
    public final void graphNode() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:236:5:
            // ( varOrTerm | triplesNode )
            int alt47 = 2;
            switch (input.LA(1)) {
            case IRI_REF:
            case PNAME_NS:
            case INTEGER:
            case VAR1:
            case VAR2:
            case DECIMAL:
            case DOUBLE:
            case INTEGER_POSITIVE:
            case DECIMAL_POSITIVE:
            case DOUBLE_POSITIVE:
            case INTEGER_NEGATIVE:
            case DECIMAL_NEGATIVE:
            case DOUBLE_NEGATIVE:
            case TRUE:
            case FALSE:
            case STRING_LITERAL1:
            case STRING_LITERAL2:
            case STRING_LITERAL_LONG1:
            case STRING_LITERAL_LONG2:
            case PNAME_LN:
            case BLANK_NODE_LABEL: {
                alt47 = 1;
            }
                break;
            case OPEN_SQUARE_BRACE: {
                int LA47_2 = input.LA(2);

                if ((LA47_2 == CLOSE_SQUARE_BRACE)) {
                    alt47 = 1;
                }
                else if ((LA47_2 == IRI_REF || LA47_2 == PNAME_NS || LA47_2 == A
                        || (LA47_2 >= VAR1 && LA47_2 <= VAR2) || LA47_2 == PNAME_LN)) {
                    alt47 = 2;
                }
                else {
                    NoViableAltException nvae = new NoViableAltException("", 47, 2, input);

                    throw nvae;
                }
            }
                break;
            case OPEN_BRACE: {
                int LA47_3 = input.LA(2);

                if ((LA47_3 == CLOSE_BRACE)) {
                    alt47 = 1;
                }
                else if ((LA47_3 == IRI_REF || LA47_3 == PNAME_NS || LA47_3 == INTEGER
                        || LA47_3 == OPEN_BRACE || LA47_3 == OPEN_SQUARE_BRACE
                        || (LA47_3 >= VAR1 && LA47_3 <= VAR2) || (LA47_3 >= DECIMAL && LA47_3 <= BLANK_NODE_LABEL))) {
                    alt47 = 2;
                }
                else {
                    NoViableAltException nvae = new NoViableAltException("", 47, 3, input);

                    throw nvae;
                }
            }
                break;
            default:
                NoViableAltException nvae = new NoViableAltException("", 47, 0, input);

                throw nvae;
            }

            switch (alt47) {
            case 1:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:236:7:
            // varOrTerm
            {
                pushFollow(FOLLOW_varOrTerm_in_graphNode1051);
                varOrTerm();

                state._fsp--;

            }
                break;
            case 2:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:236:19:
            // triplesNode
            {
                pushFollow(FOLLOW_triplesNode_in_graphNode1055);
                triplesNode();

                state._fsp--;

            }
                break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "graphNode"

    // $ANTLR start "varOrTerm"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:239:1:
    // varOrTerm : ( var | graphTerm );
    public final void varOrTerm() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:240:5:
            // ( var | graphTerm )
            int alt48 = 2;
            int LA48_0 = input.LA(1);

            if (((LA48_0 >= VAR1 && LA48_0 <= VAR2))) {
                alt48 = 1;
            }
            else if ((LA48_0 == IRI_REF || LA48_0 == PNAME_NS || LA48_0 == INTEGER
                    || LA48_0 == OPEN_BRACE || LA48_0 == OPEN_SQUARE_BRACE || (LA48_0 >= DECIMAL && LA48_0 <= BLANK_NODE_LABEL))) {
                alt48 = 2;
            }
            else {
                NoViableAltException nvae = new NoViableAltException("", 48, 0, input);

                throw nvae;
            }
            switch (alt48) {
            case 1:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:240:7:
            // var
            {
                pushFollow(FOLLOW_var_in_varOrTerm1072);
                var();

                state._fsp--;

            }
                break;
            case 2:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:241:7:
            // graphTerm
            {
                pushFollow(FOLLOW_graphTerm_in_varOrTerm1080);
                graphTerm();

                state._fsp--;

            }
                break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "varOrTerm"

    // $ANTLR start "varOrIRIref"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:244:1:
    // varOrIRIref : ( var | iriRef );
    public final void varOrIRIref() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:245:5:
            // ( var | iriRef )
            int alt49 = 2;
            int LA49_0 = input.LA(1);

            if (((LA49_0 >= VAR1 && LA49_0 <= VAR2))) {
                alt49 = 1;
            }
            else if ((LA49_0 == IRI_REF || LA49_0 == PNAME_NS || LA49_0 == PNAME_LN)) {
                alt49 = 2;
            }
            else {
                NoViableAltException nvae = new NoViableAltException("", 49, 0, input);

                throw nvae;
            }
            switch (alt49) {
            case 1:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:245:7:
            // var
            {
                pushFollow(FOLLOW_var_in_varOrIRIref1097);
                var();

                state._fsp--;

            }
                break;
            case 2:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:245:13:
            // iriRef
            {
                pushFollow(FOLLOW_iriRef_in_varOrIRIref1101);
                iriRef();

                state._fsp--;

            }
                break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "varOrIRIref"

    // $ANTLR start "var"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:248:1:
    // var : ( VAR1 | VAR2 );
    public final void var() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:249:5:
            // ( VAR1 | VAR2 )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:
            {
                if ((input.LA(1) >= VAR1 && input.LA(1) <= VAR2)) {
                    input.consume();
                    state.errorRecovery = false;
                }
                else {
                    MismatchedSetException mse = new MismatchedSetException(null, input);
                    throw mse;
                }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "var"

    // $ANTLR start "graphTerm"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:253:1:
    // graphTerm : ( iriRef | rdfLiteral | numericLiteral | booleanLiteral |
    // blankNode | OPEN_BRACE CLOSE_BRACE );
    public final void graphTerm() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:254:5:
            // ( iriRef | rdfLiteral | numericLiteral | booleanLiteral |
            // blankNode | OPEN_BRACE CLOSE_BRACE )
            int alt50 = 6;
            switch (input.LA(1)) {
            case IRI_REF:
            case PNAME_NS:
            case PNAME_LN: {
                alt50 = 1;
            }
                break;
            case STRING_LITERAL1:
            case STRING_LITERAL2:
            case STRING_LITERAL_LONG1:
            case STRING_LITERAL_LONG2: {
                alt50 = 2;
            }
                break;
            case INTEGER:
            case DECIMAL:
            case DOUBLE:
            case INTEGER_POSITIVE:
            case DECIMAL_POSITIVE:
            case DOUBLE_POSITIVE:
            case INTEGER_NEGATIVE:
            case DECIMAL_NEGATIVE:
            case DOUBLE_NEGATIVE: {
                alt50 = 3;
            }
                break;
            case TRUE:
            case FALSE: {
                alt50 = 4;
            }
                break;
            case OPEN_SQUARE_BRACE:
            case BLANK_NODE_LABEL: {
                alt50 = 5;
            }
                break;
            case OPEN_BRACE: {
                alt50 = 6;
            }
                break;
            default:
                NoViableAltException nvae = new NoViableAltException("", 50, 0, input);

                throw nvae;
            }

            switch (alt50) {
            case 1:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:254:7:
            // iriRef
            {
                pushFollow(FOLLOW_iriRef_in_graphTerm1143);
                iriRef();

                state._fsp--;

            }
                break;
            case 2:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:255:7:
            // rdfLiteral
            {
                pushFollow(FOLLOW_rdfLiteral_in_graphTerm1151);
                rdfLiteral();

                state._fsp--;

            }
                break;
            case 3:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:256:7:
            // numericLiteral
            {
                pushFollow(FOLLOW_numericLiteral_in_graphTerm1159);
                numericLiteral();

                state._fsp--;

            }
                break;
            case 4:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:257:7:
            // booleanLiteral
            {
                pushFollow(FOLLOW_booleanLiteral_in_graphTerm1167);
                booleanLiteral();

                state._fsp--;

            }
                break;
            case 5:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:258:7:
            // blankNode
            {
                pushFollow(FOLLOW_blankNode_in_graphTerm1175);
                blankNode();

                state._fsp--;

            }
                break;
            case 6:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:259:7:
            // OPEN_BRACE CLOSE_BRACE
            {
                match(input, OPEN_BRACE, FOLLOW_OPEN_BRACE_in_graphTerm1183);
                match(input, CLOSE_BRACE, FOLLOW_CLOSE_BRACE_in_graphTerm1185);

            }
                break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "graphTerm"

    // $ANTLR start "expression"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:262:1:
    // expression : conditionalOrExpression ;
    public final void expression() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:263:5:
            // ( conditionalOrExpression )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:263:7:
            // conditionalOrExpression
            {
                pushFollow(FOLLOW_conditionalOrExpression_in_expression1202);
                conditionalOrExpression();

                state._fsp--;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "expression"

    // $ANTLR start "conditionalOrExpression"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:266:1:
    // conditionalOrExpression : conditionalAndExpression ( OR
    // conditionalAndExpression )* ;
    public final void conditionalOrExpression() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:267:5:
            // ( conditionalAndExpression ( OR conditionalAndExpression )* )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:267:7:
            // conditionalAndExpression ( OR conditionalAndExpression )*
            {
                pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression1219);
                conditionalAndExpression();

                state._fsp--;

                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:267:32:
                // ( OR conditionalAndExpression )*
                loop51: do {
                    int alt51 = 2;
                    int LA51_0 = input.LA(1);

                    if ((LA51_0 == OR)) {
                        alt51 = 1;
                    }

                    switch (alt51) {
                    case 1:
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:267:34:
                    // OR conditionalAndExpression
                    {
                        match(input, OR, FOLLOW_OR_in_conditionalOrExpression1223);
                        pushFollow(FOLLOW_conditionalAndExpression_in_conditionalOrExpression1225);
                        conditionalAndExpression();

                        state._fsp--;

                    }
                        break;

                    default:
                        break loop51;
                    }
                } while (true);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "conditionalOrExpression"

    // $ANTLR start "conditionalAndExpression"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:270:1:
    // conditionalAndExpression : valueLogical ( AND valueLogical )* ;
    public final void conditionalAndExpression() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:271:5:
            // ( valueLogical ( AND valueLogical )* )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:271:7:
            // valueLogical ( AND valueLogical )*
            {
                pushFollow(FOLLOW_valueLogical_in_conditionalAndExpression1245);
                valueLogical();

                state._fsp--;

                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:271:20:
                // ( AND valueLogical )*
                loop52: do {
                    int alt52 = 2;
                    int LA52_0 = input.LA(1);

                    if ((LA52_0 == AND)) {
                        alt52 = 1;
                    }

                    switch (alt52) {
                    case 1:
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:271:22:
                    // AND valueLogical
                    {
                        match(input, AND, FOLLOW_AND_in_conditionalAndExpression1249);
                        pushFollow(FOLLOW_valueLogical_in_conditionalAndExpression1251);
                        valueLogical();

                        state._fsp--;

                    }
                        break;

                    default:
                        break loop52;
                    }
                } while (true);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "conditionalAndExpression"

    // $ANTLR start "valueLogical"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:274:1:
    // valueLogical : relationalExpression ;
    public final void valueLogical() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:275:5:
            // ( relationalExpression )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:275:7:
            // relationalExpression
            {
                pushFollow(FOLLOW_relationalExpression_in_valueLogical1271);
                relationalExpression();

                state._fsp--;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "valueLogical"

    // $ANTLR start "relationalExpression"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:278:1:
    // relationalExpression : numericExpression ( EQUAL numericExpression |
    // NOT_EQUAL numericExpression | LESS numericExpression | GREATER
    // numericExpression | LESS_EQUAL numericExpression | GREATER_EQUAL
    // numericExpression )? ;
    public final void relationalExpression() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:279:5:
            // ( numericExpression ( EQUAL numericExpression | NOT_EQUAL
            // numericExpression | LESS numericExpression | GREATER
            // numericExpression | LESS_EQUAL numericExpression | GREATER_EQUAL
            // numericExpression )? )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:279:7:
            // numericExpression ( EQUAL numericExpression | NOT_EQUAL
            // numericExpression | LESS numericExpression | GREATER
            // numericExpression | LESS_EQUAL numericExpression | GREATER_EQUAL
            // numericExpression )?
            {
                pushFollow(FOLLOW_numericExpression_in_relationalExpression1288);
                numericExpression();

                state._fsp--;

                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:279:25:
                // ( EQUAL numericExpression | NOT_EQUAL numericExpression |
                // LESS numericExpression | GREATER numericExpression |
                // LESS_EQUAL numericExpression | GREATER_EQUAL
                // numericExpression )?
                int alt53 = 7;
                switch (input.LA(1)) {
                case EQUAL: {
                    alt53 = 1;
                }
                    break;
                case NOT_EQUAL: {
                    alt53 = 2;
                }
                    break;
                case LESS: {
                    alt53 = 3;
                }
                    break;
                case GREATER: {
                    alt53 = 4;
                }
                    break;
                case LESS_EQUAL: {
                    alt53 = 5;
                }
                    break;
                case GREATER_EQUAL: {
                    alt53 = 6;
                }
                    break;
                }

                switch (alt53) {
                case 1:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:279:27:
                // EQUAL numericExpression
                {
                    match(input, EQUAL, FOLLOW_EQUAL_in_relationalExpression1292);
                    pushFollow(FOLLOW_numericExpression_in_relationalExpression1294);
                    numericExpression();

                    state._fsp--;

                }
                    break;
                case 2:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:279:53:
                // NOT_EQUAL numericExpression
                {
                    match(input, NOT_EQUAL, FOLLOW_NOT_EQUAL_in_relationalExpression1298);
                    pushFollow(FOLLOW_numericExpression_in_relationalExpression1300);
                    numericExpression();

                    state._fsp--;

                }
                    break;
                case 3:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:279:83:
                // LESS numericExpression
                {
                    match(input, LESS, FOLLOW_LESS_in_relationalExpression1304);
                    pushFollow(FOLLOW_numericExpression_in_relationalExpression1306);
                    numericExpression();

                    state._fsp--;

                }
                    break;
                case 4:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:279:108:
                // GREATER numericExpression
                {
                    match(input, GREATER, FOLLOW_GREATER_in_relationalExpression1310);
                    pushFollow(FOLLOW_numericExpression_in_relationalExpression1312);
                    numericExpression();

                    state._fsp--;

                }
                    break;
                case 5:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:279:136:
                // LESS_EQUAL numericExpression
                {
                    match(input, LESS_EQUAL, FOLLOW_LESS_EQUAL_in_relationalExpression1316);
                    pushFollow(FOLLOW_numericExpression_in_relationalExpression1318);
                    numericExpression();

                    state._fsp--;

                }
                    break;
                case 6:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:279:167:
                // GREATER_EQUAL numericExpression
                {
                    match(input, GREATER_EQUAL, FOLLOW_GREATER_EQUAL_in_relationalExpression1322);
                    pushFollow(FOLLOW_numericExpression_in_relationalExpression1324);
                    numericExpression();

                    state._fsp--;

                }
                    break;

                }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "relationalExpression"

    // $ANTLR start "numericExpression"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:282:1:
    // numericExpression : additiveExpression ;
    public final void numericExpression() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:283:5:
            // ( additiveExpression )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:283:7:
            // additiveExpression
            {
                pushFollow(FOLLOW_additiveExpression_in_numericExpression1344);
                additiveExpression();

                state._fsp--;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "numericExpression"

    // $ANTLR start "additiveExpression"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:286:1:
    // additiveExpression : multiplicativeExpression ( PLUS
    // multiplicativeExpression | MINUS multiplicativeExpression |
    // numericLiteralPositive | numericLiteralNegative )* ;
    public final void additiveExpression() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:287:5:
            // ( multiplicativeExpression ( PLUS multiplicativeExpression |
            // MINUS multiplicativeExpression | numericLiteralPositive |
            // numericLiteralNegative )* )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:287:7:
            // multiplicativeExpression ( PLUS multiplicativeExpression | MINUS
            // multiplicativeExpression | numericLiteralPositive |
            // numericLiteralNegative )*
            {
                pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression1361);
                multiplicativeExpression();

                state._fsp--;

                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:287:32:
                // ( PLUS multiplicativeExpression | MINUS
                // multiplicativeExpression | numericLiteralPositive |
                // numericLiteralNegative )*
                loop54: do {
                    int alt54 = 5;
                    switch (input.LA(1)) {
                    case PLUS: {
                        alt54 = 1;
                    }
                        break;
                    case MINUS: {
                        alt54 = 2;
                    }
                        break;
                    case INTEGER_POSITIVE:
                    case DECIMAL_POSITIVE:
                    case DOUBLE_POSITIVE: {
                        alt54 = 3;
                    }
                        break;
                    case INTEGER_NEGATIVE:
                    case DECIMAL_NEGATIVE:
                    case DOUBLE_NEGATIVE: {
                        alt54 = 4;
                    }
                        break;

                    }

                    switch (alt54) {
                    case 1:
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:287:34:
                    // PLUS multiplicativeExpression
                    {
                        match(input, PLUS, FOLLOW_PLUS_in_additiveExpression1365);
                        pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression1367);
                        multiplicativeExpression();

                        state._fsp--;

                    }
                        break;
                    case 2:
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:287:66:
                    // MINUS multiplicativeExpression
                    {
                        match(input, MINUS, FOLLOW_MINUS_in_additiveExpression1371);
                        pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression1373);
                        multiplicativeExpression();

                        state._fsp--;

                    }
                        break;
                    case 3:
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:287:99:
                    // numericLiteralPositive
                    {
                        pushFollow(FOLLOW_numericLiteralPositive_in_additiveExpression1377);
                        numericLiteralPositive();

                        state._fsp--;

                    }
                        break;
                    case 4:
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:287:124:
                    // numericLiteralNegative
                    {
                        pushFollow(FOLLOW_numericLiteralNegative_in_additiveExpression1381);
                        numericLiteralNegative();

                        state._fsp--;

                    }
                        break;

                    default:
                        break loop54;
                    }
                } while (true);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "additiveExpression"

    // $ANTLR start "multiplicativeExpression"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:290:1:
    // multiplicativeExpression : unaryExpression ( ASTERISK unaryExpression |
    // DIVIDE unaryExpression )* ;
    public final void multiplicativeExpression() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:291:5:
            // ( unaryExpression ( ASTERISK unaryExpression | DIVIDE
            // unaryExpression )* )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:291:7:
            // unaryExpression ( ASTERISK unaryExpression | DIVIDE
            // unaryExpression )*
            {
                pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression1401);
                unaryExpression();

                state._fsp--;

                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:291:23:
                // ( ASTERISK unaryExpression | DIVIDE unaryExpression )*
                loop55: do {
                    int alt55 = 3;
                    int LA55_0 = input.LA(1);

                    if ((LA55_0 == ASTERISK)) {
                        alt55 = 1;
                    }
                    else if ((LA55_0 == DIVIDE)) {
                        alt55 = 2;
                    }

                    switch (alt55) {
                    case 1:
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:291:25:
                    // ASTERISK unaryExpression
                    {
                        match(input, ASTERISK, FOLLOW_ASTERISK_in_multiplicativeExpression1405);
                        pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression1407);
                        unaryExpression();

                        state._fsp--;

                    }
                        break;
                    case 2:
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:291:52:
                    // DIVIDE unaryExpression
                    {
                        match(input, DIVIDE, FOLLOW_DIVIDE_in_multiplicativeExpression1411);
                        pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression1413);
                        unaryExpression();

                        state._fsp--;

                    }
                        break;

                    default:
                        break loop55;
                    }
                } while (true);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "multiplicativeExpression"

    // $ANTLR start "unaryExpression"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:294:1:
    // unaryExpression : ( NOT primaryExpression | PLUS primaryExpression |
    // MINUS primaryExpression | primaryExpression );
    public final void unaryExpression() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:295:5:
            // ( NOT primaryExpression | PLUS primaryExpression | MINUS
            // primaryExpression | primaryExpression )
            int alt56 = 4;
            switch (input.LA(1)) {
            case NOT: {
                alt56 = 1;
            }
                break;
            case PLUS: {
                alt56 = 2;
            }
                break;
            case MINUS: {
                alt56 = 3;
            }
                break;
            case IRI_REF:
            case PNAME_NS:
            case INTEGER:
            case OPEN_BRACE:
            case VAR1:
            case VAR2:
            case STR:
            case LANG:
            case LANGMATCHES:
            case DATATYPE:
            case BOUND:
            case SAMETERM:
            case ISIRI:
            case ISURI:
            case ISBLANK:
            case ISLITERAL:
            case REGEX:
            case DECIMAL:
            case DOUBLE:
            case INTEGER_POSITIVE:
            case DECIMAL_POSITIVE:
            case DOUBLE_POSITIVE:
            case INTEGER_NEGATIVE:
            case DECIMAL_NEGATIVE:
            case DOUBLE_NEGATIVE:
            case TRUE:
            case FALSE:
            case STRING_LITERAL1:
            case STRING_LITERAL2:
            case STRING_LITERAL_LONG1:
            case STRING_LITERAL_LONG2:
            case PNAME_LN: {
                alt56 = 4;
            }
                break;
            default:
                NoViableAltException nvae = new NoViableAltException("", 56, 0, input);

                throw nvae;
            }

            switch (alt56) {
            case 1:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:295:7:
            // NOT primaryExpression
            {
                match(input, NOT, FOLLOW_NOT_in_unaryExpression1433);
                pushFollow(FOLLOW_primaryExpression_in_unaryExpression1435);
                primaryExpression();

                state._fsp--;

            }
                break;
            case 2:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:296:7:
            // PLUS primaryExpression
            {
                match(input, PLUS, FOLLOW_PLUS_in_unaryExpression1443);
                pushFollow(FOLLOW_primaryExpression_in_unaryExpression1445);
                primaryExpression();

                state._fsp--;

            }
                break;
            case 3:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:297:7:
            // MINUS primaryExpression
            {
                match(input, MINUS, FOLLOW_MINUS_in_unaryExpression1453);
                pushFollow(FOLLOW_primaryExpression_in_unaryExpression1455);
                primaryExpression();

                state._fsp--;

            }
                break;
            case 4:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:298:7:
            // primaryExpression
            {
                pushFollow(FOLLOW_primaryExpression_in_unaryExpression1463);
                primaryExpression();

                state._fsp--;

            }
                break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "unaryExpression"

    // $ANTLR start "primaryExpression"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:301:1:
    // primaryExpression : ( brackettedExpression | builtInCall |
    // iriRefOrFunction | rdfLiteral | numericLiteral | booleanLiteral | var );
    public final void primaryExpression() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:302:5:
            // ( brackettedExpression | builtInCall | iriRefOrFunction |
            // rdfLiteral | numericLiteral | booleanLiteral | var )
            int alt57 = 7;
            switch (input.LA(1)) {
            case OPEN_BRACE: {
                alt57 = 1;
            }
                break;
            case STR:
            case LANG:
            case LANGMATCHES:
            case DATATYPE:
            case BOUND:
            case SAMETERM:
            case ISIRI:
            case ISURI:
            case ISBLANK:
            case ISLITERAL:
            case REGEX: {
                alt57 = 2;
            }
                break;
            case IRI_REF:
            case PNAME_NS:
            case PNAME_LN: {
                alt57 = 3;
            }
                break;
            case STRING_LITERAL1:
            case STRING_LITERAL2:
            case STRING_LITERAL_LONG1:
            case STRING_LITERAL_LONG2: {
                alt57 = 4;
            }
                break;
            case INTEGER:
            case DECIMAL:
            case DOUBLE:
            case INTEGER_POSITIVE:
            case DECIMAL_POSITIVE:
            case DOUBLE_POSITIVE:
            case INTEGER_NEGATIVE:
            case DECIMAL_NEGATIVE:
            case DOUBLE_NEGATIVE: {
                alt57 = 5;
            }
                break;
            case TRUE:
            case FALSE: {
                alt57 = 6;
            }
                break;
            case VAR1:
            case VAR2: {
                alt57 = 7;
            }
                break;
            default:
                NoViableAltException nvae = new NoViableAltException("", 57, 0, input);

                throw nvae;
            }

            switch (alt57) {
            case 1:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:302:7:
            // brackettedExpression
            {
                pushFollow(FOLLOW_brackettedExpression_in_primaryExpression1480);
                brackettedExpression();

                state._fsp--;

            }
                break;
            case 2:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:302:30:
            // builtInCall
            {
                pushFollow(FOLLOW_builtInCall_in_primaryExpression1484);
                builtInCall();

                state._fsp--;

            }
                break;
            case 3:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:302:44:
            // iriRefOrFunction
            {
                pushFollow(FOLLOW_iriRefOrFunction_in_primaryExpression1488);
                iriRefOrFunction();

                state._fsp--;

            }
                break;
            case 4:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:302:63:
            // rdfLiteral
            {
                pushFollow(FOLLOW_rdfLiteral_in_primaryExpression1492);
                rdfLiteral();

                state._fsp--;

            }
                break;
            case 5:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:302:76:
            // numericLiteral
            {
                pushFollow(FOLLOW_numericLiteral_in_primaryExpression1496);
                numericLiteral();

                state._fsp--;

            }
                break;
            case 6:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:302:93:
            // booleanLiteral
            {
                pushFollow(FOLLOW_booleanLiteral_in_primaryExpression1500);
                booleanLiteral();

                state._fsp--;

            }
                break;
            case 7:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:302:110:
            // var
            {
                pushFollow(FOLLOW_var_in_primaryExpression1504);
                var();

                state._fsp--;

            }
                break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "primaryExpression"

    // $ANTLR start "brackettedExpression"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:305:1:
    // brackettedExpression : OPEN_BRACE expression CLOSE_BRACE ;
    public final void brackettedExpression() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:306:5:
            // ( OPEN_BRACE expression CLOSE_BRACE )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:306:7:
            // OPEN_BRACE expression CLOSE_BRACE
            {
                match(input, OPEN_BRACE, FOLLOW_OPEN_BRACE_in_brackettedExpression1521);
                pushFollow(FOLLOW_expression_in_brackettedExpression1523);
                expression();

                state._fsp--;

                match(input, CLOSE_BRACE, FOLLOW_CLOSE_BRACE_in_brackettedExpression1525);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "brackettedExpression"

    // $ANTLR start "builtInCall"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:309:1:
    // builtInCall : ( STR OPEN_BRACE expression CLOSE_BRACE | LANG OPEN_BRACE
    // expression CLOSE_BRACE | LANGMATCHES OPEN_BRACE expression COMMA
    // expression CLOSE_BRACE | DATATYPE OPEN_BRACE expression CLOSE_BRACE |
    // BOUND OPEN_BRACE var CLOSE_BRACE | SAMETERM OPEN_BRACE expression COMMA
    // expression CLOSE_BRACE | ISIRI OPEN_BRACE expression CLOSE_BRACE | ISURI
    // OPEN_BRACE expression CLOSE_BRACE | ISBLANK OPEN_BRACE expression
    // CLOSE_BRACE | ISLITERAL OPEN_BRACE expression CLOSE_BRACE |
    // regexExpression );
    public final void builtInCall() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:310:5:
            // ( STR OPEN_BRACE expression CLOSE_BRACE | LANG OPEN_BRACE
            // expression CLOSE_BRACE | LANGMATCHES OPEN_BRACE expression COMMA
            // expression CLOSE_BRACE | DATATYPE OPEN_BRACE expression
            // CLOSE_BRACE | BOUND OPEN_BRACE var CLOSE_BRACE | SAMETERM
            // OPEN_BRACE expression COMMA expression CLOSE_BRACE | ISIRI
            // OPEN_BRACE expression CLOSE_BRACE | ISURI OPEN_BRACE expression
            // CLOSE_BRACE | ISBLANK OPEN_BRACE expression CLOSE_BRACE |
            // ISLITERAL OPEN_BRACE expression CLOSE_BRACE | regexExpression )
            int alt58 = 11;
            switch (input.LA(1)) {
            case STR: {
                alt58 = 1;
            }
                break;
            case LANG: {
                alt58 = 2;
            }
                break;
            case LANGMATCHES: {
                alt58 = 3;
            }
                break;
            case DATATYPE: {
                alt58 = 4;
            }
                break;
            case BOUND: {
                alt58 = 5;
            }
                break;
            case SAMETERM: {
                alt58 = 6;
            }
                break;
            case ISIRI: {
                alt58 = 7;
            }
                break;
            case ISURI: {
                alt58 = 8;
            }
                break;
            case ISBLANK: {
                alt58 = 9;
            }
                break;
            case ISLITERAL: {
                alt58 = 10;
            }
                break;
            case REGEX: {
                alt58 = 11;
            }
                break;
            default:
                NoViableAltException nvae = new NoViableAltException("", 58, 0, input);

                throw nvae;
            }

            switch (alt58) {
            case 1:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:310:7:
            // STR OPEN_BRACE expression CLOSE_BRACE
            {
                match(input, STR, FOLLOW_STR_in_builtInCall1542);
                match(input, OPEN_BRACE, FOLLOW_OPEN_BRACE_in_builtInCall1544);
                pushFollow(FOLLOW_expression_in_builtInCall1546);
                expression();

                state._fsp--;

                match(input, CLOSE_BRACE, FOLLOW_CLOSE_BRACE_in_builtInCall1548);

            }
                break;
            case 2:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:311:7:
            // LANG OPEN_BRACE expression CLOSE_BRACE
            {
                match(input, LANG, FOLLOW_LANG_in_builtInCall1556);
                match(input, OPEN_BRACE, FOLLOW_OPEN_BRACE_in_builtInCall1558);
                pushFollow(FOLLOW_expression_in_builtInCall1560);
                expression();

                state._fsp--;

                match(input, CLOSE_BRACE, FOLLOW_CLOSE_BRACE_in_builtInCall1562);

            }
                break;
            case 3:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:312:7:
            // LANGMATCHES OPEN_BRACE expression COMMA expression CLOSE_BRACE
            {
                match(input, LANGMATCHES, FOLLOW_LANGMATCHES_in_builtInCall1570);
                match(input, OPEN_BRACE, FOLLOW_OPEN_BRACE_in_builtInCall1572);
                pushFollow(FOLLOW_expression_in_builtInCall1574);
                expression();

                state._fsp--;

                match(input, COMMA, FOLLOW_COMMA_in_builtInCall1576);
                pushFollow(FOLLOW_expression_in_builtInCall1578);
                expression();

                state._fsp--;

                match(input, CLOSE_BRACE, FOLLOW_CLOSE_BRACE_in_builtInCall1580);

            }
                break;
            case 4:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:313:7:
            // DATATYPE OPEN_BRACE expression CLOSE_BRACE
            {
                match(input, DATATYPE, FOLLOW_DATATYPE_in_builtInCall1588);
                match(input, OPEN_BRACE, FOLLOW_OPEN_BRACE_in_builtInCall1590);
                pushFollow(FOLLOW_expression_in_builtInCall1592);
                expression();

                state._fsp--;

                match(input, CLOSE_BRACE, FOLLOW_CLOSE_BRACE_in_builtInCall1594);

            }
                break;
            case 5:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:314:7:
            // BOUND OPEN_BRACE var CLOSE_BRACE
            {
                match(input, BOUND, FOLLOW_BOUND_in_builtInCall1602);
                match(input, OPEN_BRACE, FOLLOW_OPEN_BRACE_in_builtInCall1604);
                pushFollow(FOLLOW_var_in_builtInCall1606);
                var();

                state._fsp--;

                match(input, CLOSE_BRACE, FOLLOW_CLOSE_BRACE_in_builtInCall1608);

            }
                break;
            case 6:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:315:7:
            // SAMETERM OPEN_BRACE expression COMMA expression CLOSE_BRACE
            {
                match(input, SAMETERM, FOLLOW_SAMETERM_in_builtInCall1616);
                match(input, OPEN_BRACE, FOLLOW_OPEN_BRACE_in_builtInCall1618);
                pushFollow(FOLLOW_expression_in_builtInCall1620);
                expression();

                state._fsp--;

                match(input, COMMA, FOLLOW_COMMA_in_builtInCall1622);
                pushFollow(FOLLOW_expression_in_builtInCall1624);
                expression();

                state._fsp--;

                match(input, CLOSE_BRACE, FOLLOW_CLOSE_BRACE_in_builtInCall1626);

            }
                break;
            case 7:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:316:7:
            // ISIRI OPEN_BRACE expression CLOSE_BRACE
            {
                match(input, ISIRI, FOLLOW_ISIRI_in_builtInCall1634);
                match(input, OPEN_BRACE, FOLLOW_OPEN_BRACE_in_builtInCall1636);
                pushFollow(FOLLOW_expression_in_builtInCall1638);
                expression();

                state._fsp--;

                match(input, CLOSE_BRACE, FOLLOW_CLOSE_BRACE_in_builtInCall1640);

            }
                break;
            case 8:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:317:7:
            // ISURI OPEN_BRACE expression CLOSE_BRACE
            {
                match(input, ISURI, FOLLOW_ISURI_in_builtInCall1648);
                match(input, OPEN_BRACE, FOLLOW_OPEN_BRACE_in_builtInCall1650);
                pushFollow(FOLLOW_expression_in_builtInCall1652);
                expression();

                state._fsp--;

                match(input, CLOSE_BRACE, FOLLOW_CLOSE_BRACE_in_builtInCall1654);

            }
                break;
            case 9:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:318:7:
            // ISBLANK OPEN_BRACE expression CLOSE_BRACE
            {
                match(input, ISBLANK, FOLLOW_ISBLANK_in_builtInCall1662);
                match(input, OPEN_BRACE, FOLLOW_OPEN_BRACE_in_builtInCall1664);
                pushFollow(FOLLOW_expression_in_builtInCall1666);
                expression();

                state._fsp--;

                match(input, CLOSE_BRACE, FOLLOW_CLOSE_BRACE_in_builtInCall1668);

            }
                break;
            case 10:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:319:7:
            // ISLITERAL OPEN_BRACE expression CLOSE_BRACE
            {
                match(input, ISLITERAL, FOLLOW_ISLITERAL_in_builtInCall1676);
                match(input, OPEN_BRACE, FOLLOW_OPEN_BRACE_in_builtInCall1678);
                pushFollow(FOLLOW_expression_in_builtInCall1680);
                expression();

                state._fsp--;

                match(input, CLOSE_BRACE, FOLLOW_CLOSE_BRACE_in_builtInCall1682);

            }
                break;
            case 11:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:320:7:
            // regexExpression
            {
                pushFollow(FOLLOW_regexExpression_in_builtInCall1690);
                regexExpression();

                state._fsp--;

            }
                break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "builtInCall"

    // $ANTLR start "regexExpression"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:323:1:
    // regexExpression : REGEX OPEN_BRACE expression COMMA expression ( COMMA
    // expression )? CLOSE_BRACE ;
    public final void regexExpression() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:324:5:
            // ( REGEX OPEN_BRACE expression COMMA expression ( COMMA expression
            // )? CLOSE_BRACE )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:324:7:
            // REGEX OPEN_BRACE expression COMMA expression ( COMMA expression
            // )? CLOSE_BRACE
            {
                match(input, REGEX, FOLLOW_REGEX_in_regexExpression1707);
                match(input, OPEN_BRACE, FOLLOW_OPEN_BRACE_in_regexExpression1709);
                pushFollow(FOLLOW_expression_in_regexExpression1711);
                expression();

                state._fsp--;

                match(input, COMMA, FOLLOW_COMMA_in_regexExpression1713);
                pushFollow(FOLLOW_expression_in_regexExpression1715);
                expression();

                state._fsp--;

                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:324:52:
                // ( COMMA expression )?
                int alt59 = 2;
                int LA59_0 = input.LA(1);

                if ((LA59_0 == COMMA)) {
                    alt59 = 1;
                }
                switch (alt59) {
                case 1:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:324:54:
                // COMMA expression
                {
                    match(input, COMMA, FOLLOW_COMMA_in_regexExpression1719);
                    pushFollow(FOLLOW_expression_in_regexExpression1721);
                    expression();

                    state._fsp--;

                }
                    break;

                }

                match(input, CLOSE_BRACE, FOLLOW_CLOSE_BRACE_in_regexExpression1726);

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "regexExpression"

    // $ANTLR start "iriRefOrFunction"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:327:1:
    // iriRefOrFunction : iriRef ( argList )? ;
    public final void iriRefOrFunction() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:328:5:
            // ( iriRef ( argList )? )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:328:7:
            // iriRef ( argList )?
            {
                pushFollow(FOLLOW_iriRef_in_iriRefOrFunction1743);
                iriRef();

                state._fsp--;

                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:328:14:
                // ( argList )?
                int alt60 = 2;
                int LA60_0 = input.LA(1);

                if ((LA60_0 == OPEN_BRACE)) {
                    alt60 = 1;
                }
                switch (alt60) {
                case 1:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:328:14:
                // argList
                {
                    pushFollow(FOLLOW_argList_in_iriRefOrFunction1745);
                    argList();

                    state._fsp--;

                }
                    break;

                }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "iriRefOrFunction"

    // $ANTLR start "rdfLiteral"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:331:1:
    // rdfLiteral : string ( LANGTAG | ( REFERENCE iriRef ) )? ;
    public final void rdfLiteral() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:332:5:
            // ( string ( LANGTAG | ( REFERENCE iriRef ) )? )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:332:7:
            // string ( LANGTAG | ( REFERENCE iriRef ) )?
            {
                pushFollow(FOLLOW_string_in_rdfLiteral1763);
                string();

                state._fsp--;

                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:332:14:
                // ( LANGTAG | ( REFERENCE iriRef ) )?
                int alt61 = 3;
                int LA61_0 = input.LA(1);

                if ((LA61_0 == LANGTAG)) {
                    alt61 = 1;
                }
                else if ((LA61_0 == REFERENCE)) {
                    alt61 = 2;
                }
                switch (alt61) {
                case 1:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:332:16:
                // LANGTAG
                {
                    match(input, LANGTAG, FOLLOW_LANGTAG_in_rdfLiteral1767);

                }
                    break;
                case 2:
                // C:\\Users\\Mike
                // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:332:26:
                // ( REFERENCE iriRef )
                {
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:332:26:
                    // ( REFERENCE iriRef )
                    // C:\\Users\\Mike
                    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:332:28:
                    // REFERENCE iriRef
                    {
                        match(input, REFERENCE, FOLLOW_REFERENCE_in_rdfLiteral1773);
                        pushFollow(FOLLOW_iriRef_in_rdfLiteral1775);
                        iriRef();

                        state._fsp--;

                    }

                }
                    break;

                }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "rdfLiteral"

    // $ANTLR start "numericLiteral"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:335:1:
    // numericLiteral : ( numericLiteralUnsigned | numericLiteralPositive |
    // numericLiteralNegative );
    public final void numericLiteral() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:336:5:
            // ( numericLiteralUnsigned | numericLiteralPositive |
            // numericLiteralNegative )
            int alt62 = 3;
            switch (input.LA(1)) {
            case INTEGER:
            case DECIMAL:
            case DOUBLE: {
                alt62 = 1;
            }
                break;
            case INTEGER_POSITIVE:
            case DECIMAL_POSITIVE:
            case DOUBLE_POSITIVE: {
                alt62 = 2;
            }
                break;
            case INTEGER_NEGATIVE:
            case DECIMAL_NEGATIVE:
            case DOUBLE_NEGATIVE: {
                alt62 = 3;
            }
                break;
            default:
                NoViableAltException nvae = new NoViableAltException("", 62, 0, input);

                throw nvae;
            }

            switch (alt62) {
            case 1:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:336:7:
            // numericLiteralUnsigned
            {
                pushFollow(FOLLOW_numericLiteralUnsigned_in_numericLiteral1797);
                numericLiteralUnsigned();

                state._fsp--;

            }
                break;
            case 2:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:336:32:
            // numericLiteralPositive
            {
                pushFollow(FOLLOW_numericLiteralPositive_in_numericLiteral1801);
                numericLiteralPositive();

                state._fsp--;

            }
                break;
            case 3:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:336:57:
            // numericLiteralNegative
            {
                pushFollow(FOLLOW_numericLiteralNegative_in_numericLiteral1805);
                numericLiteralNegative();

                state._fsp--;

            }
                break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "numericLiteral"

    // $ANTLR start "numericLiteralUnsigned"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:339:1:
    // numericLiteralUnsigned : ( INTEGER | DECIMAL | DOUBLE );
    public final void numericLiteralUnsigned() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:340:5:
            // ( INTEGER | DECIMAL | DOUBLE )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:
            {
                if (input.LA(1) == INTEGER || (input.LA(1) >= DECIMAL && input.LA(1) <= DOUBLE)) {
                    input.consume();
                    state.errorRecovery = false;
                }
                else {
                    MismatchedSetException mse = new MismatchedSetException(null, input);
                    throw mse;
                }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "numericLiteralUnsigned"

    // $ANTLR start "numericLiteralPositive"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:345:1:
    // numericLiteralPositive : ( INTEGER_POSITIVE | DECIMAL_POSITIVE |
    // DOUBLE_POSITIVE );
    public final void numericLiteralPositive() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:346:5:
            // ( INTEGER_POSITIVE | DECIMAL_POSITIVE | DOUBLE_POSITIVE )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:
            {
                if ((input.LA(1) >= INTEGER_POSITIVE && input.LA(1) <= DOUBLE_POSITIVE)) {
                    input.consume();
                    state.errorRecovery = false;
                }
                else {
                    MismatchedSetException mse = new MismatchedSetException(null, input);
                    throw mse;
                }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "numericLiteralPositive"

    // $ANTLR start "numericLiteralNegative"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:351:1:
    // numericLiteralNegative : ( INTEGER_NEGATIVE | DECIMAL_NEGATIVE |
    // DOUBLE_NEGATIVE );
    public final void numericLiteralNegative() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:352:5:
            // ( INTEGER_NEGATIVE | DECIMAL_NEGATIVE | DOUBLE_NEGATIVE )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:
            {
                if ((input.LA(1) >= INTEGER_NEGATIVE && input.LA(1) <= DOUBLE_NEGATIVE)) {
                    input.consume();
                    state.errorRecovery = false;
                }
                else {
                    MismatchedSetException mse = new MismatchedSetException(null, input);
                    throw mse;
                }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "numericLiteralNegative"

    // $ANTLR start "booleanLiteral"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:357:1:
    // booleanLiteral : ( TRUE | FALSE );
    public final void booleanLiteral() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:358:5:
            // ( TRUE | FALSE )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:
            {
                if ((input.LA(1) >= TRUE && input.LA(1) <= FALSE)) {
                    input.consume();
                    state.errorRecovery = false;
                }
                else {
                    MismatchedSetException mse = new MismatchedSetException(null, input);
                    throw mse;
                }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "booleanLiteral"

    // $ANTLR start "string"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:362:1:
    // string : ( STRING_LITERAL1 | STRING_LITERAL2 | STRING_LITERAL_LONG1 |
    // STRING_LITERAL_LONG2 );
    public final void string() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:363:5:
            // ( STRING_LITERAL1 | STRING_LITERAL2 | STRING_LITERAL_LONG1 |
            // STRING_LITERAL_LONG2 )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:
            {
                if ((input.LA(1) >= STRING_LITERAL1 && input.LA(1) <= STRING_LITERAL_LONG2)) {
                    input.consume();
                    state.errorRecovery = false;
                }
                else {
                    MismatchedSetException mse = new MismatchedSetException(null, input);
                    throw mse;
                }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "string"

    // $ANTLR start "iriRef"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:369:1:
    // iriRef : ( IRI_REF | prefixedName );
    public final void iriRef() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:370:5:
            // ( IRI_REF | prefixedName )
            int alt63 = 2;
            int LA63_0 = input.LA(1);

            if ((LA63_0 == IRI_REF)) {
                alt63 = 1;
            }
            else if ((LA63_0 == PNAME_NS || LA63_0 == PNAME_LN)) {
                alt63 = 2;
            }
            else {
                NoViableAltException nvae = new NoViableAltException("", 63, 0, input);

                throw nvae;
            }
            switch (alt63) {
            case 1:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:370:7:
            // IRI_REF
            {
                match(input, IRI_REF, FOLLOW_IRI_REF_in_iriRef1987);

            }
                break;
            case 2:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:371:7:
            // prefixedName
            {
                pushFollow(FOLLOW_prefixedName_in_iriRef1995);
                prefixedName();

                state._fsp--;

            }
                break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "iriRef"

    // $ANTLR start "prefixedName"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:374:1:
    // prefixedName : ( PNAME_LN | PNAME_NS );
    public final void prefixedName() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:375:5:
            // ( PNAME_LN | PNAME_NS )
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:
            {
                if (input.LA(1) == PNAME_NS || input.LA(1) == PNAME_LN) {
                    input.consume();
                    state.errorRecovery = false;
                }
                else {
                    MismatchedSetException mse = new MismatchedSetException(null, input);
                    throw mse;
                }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "prefixedName"

    // $ANTLR start "blankNode"
    // C:\\Users\\Mike
    // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:379:1:
    // blankNode : ( BLANK_NODE_LABEL | OPEN_SQUARE_BRACE CLOSE_SQUARE_BRACE );
    public final void blankNode() throws RecognitionException {
        try {
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:380:5:
            // ( BLANK_NODE_LABEL | OPEN_SQUARE_BRACE CLOSE_SQUARE_BRACE )
            int alt64 = 2;
            int LA64_0 = input.LA(1);

            if ((LA64_0 == BLANK_NODE_LABEL)) {
                alt64 = 1;
            }
            else if ((LA64_0 == OPEN_SQUARE_BRACE)) {
                alt64 = 2;
            }
            else {
                NoViableAltException nvae = new NoViableAltException("", 64, 0, input);

                throw nvae;
            }
            switch (alt64) {
            case 1:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:380:7:
            // BLANK_NODE_LABEL
            {
                match(input, BLANK_NODE_LABEL, FOLLOW_BLANK_NODE_LABEL_in_blankNode2037);

            }
                break;
            case 2:
            // C:\\Users\\Mike
            // Henrichs\\workspace\\com.semmtech.grammars\\src\\grammars\\sparql\\Sparql.g:381:7:
            // OPEN_SQUARE_BRACE CLOSE_SQUARE_BRACE
            {
                match(input, OPEN_SQUARE_BRACE, FOLLOW_OPEN_SQUARE_BRACE_in_blankNode2045);
                match(input, CLOSE_SQUARE_BRACE, FOLLOW_CLOSE_SQUARE_BRACE_in_blankNode2047);

            }
                break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input, re);
        }
        finally {
        }
        return;
    }

    // $ANTLR end "blankNode"

    // Delegated rules

    public static final BitSet FOLLOW_prologue_in_query36 = new BitSet(
            new long[] { 0x0000000000007100L });
    public static final BitSet FOLLOW_selectQuery_in_query40 = new BitSet(
            new long[] { 0x0000000000000000L });
    public static final BitSet FOLLOW_constructQuery_in_query44 = new BitSet(
            new long[] { 0x0000000000000000L });
    public static final BitSet FOLLOW_describeQuery_in_query48 = new BitSet(
            new long[] { 0x0000000000000000L });
    public static final BitSet FOLLOW_askQuery_in_query52 = new BitSet(
            new long[] { 0x0000000000000000L });
    public static final BitSet FOLLOW_EOF_in_query56 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_baseDecl_in_prologue73 = new BitSet(
            new long[] { 0x0000000000000042L });
    public static final BitSet FOLLOW_prefixDecl_in_prologue76 = new BitSet(
            new long[] { 0x0000000000000042L });
    public static final BitSet FOLLOW_BASE_in_baseDecl95 = new BitSet(
            new long[] { 0x0000000000000020L });
    public static final BitSet FOLLOW_IRI_REF_in_baseDecl97 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_PREFIX_in_prefixDecl114 = new BitSet(
            new long[] { 0x0000000000000080L });
    public static final BitSet FOLLOW_PNAME_NS_in_prefixDecl116 = new BitSet(
            new long[] { 0x0000000000000020L });
    public static final BitSet FOLLOW_IRI_REF_in_prefixDecl118 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_SELECT_in_selectQuery135 = new BitSet(
            new long[] { 0x0000018000000E00L });
    public static final BitSet FOLLOW_set_in_selectQuery137 = new BitSet(
            new long[] { 0x0000018000000800L });
    public static final BitSet FOLLOW_var_in_selectQuery150 = new BitSet(
            new long[] { 0x0000018002028000L });
    public static final BitSet FOLLOW_ASTERISK_in_selectQuery155 = new BitSet(
            new long[] { 0x0000000002028000L });
    public static final BitSet FOLLOW_datasetClause_in_selectQuery159 = new BitSet(
            new long[] { 0x0000000002028000L });
    public static final BitSet FOLLOW_whereClause_in_selectQuery162 = new BitSet(
            new long[] { 0x0000000001440000L });
    public static final BitSet FOLLOW_solutionModifier_in_selectQuery164 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_CONSTRUCT_in_constructQuery181 = new BitSet(
            new long[] { 0x0000000002000000L });
    public static final BitSet FOLLOW_constructTemplate_in_constructQuery183 = new BitSet(
            new long[] { 0x0000000002028000L });
    public static final BitSet FOLLOW_datasetClause_in_constructQuery185 = new BitSet(
            new long[] { 0x0000000002028000L });
    public static final BitSet FOLLOW_whereClause_in_constructQuery188 = new BitSet(
            new long[] { 0x0000000001440000L });
    public static final BitSet FOLLOW_solutionModifier_in_constructQuery190 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_DESCRIBE_in_describeQuery207 = new BitSet(new long[] {
            0x00000180000008A0L, 0x0000000000010000L });
    public static final BitSet FOLLOW_varOrIRIref_in_describeQuery211 = new BitSet(new long[] {
            0x00000180034680A0L, 0x0000000000010000L });
    public static final BitSet FOLLOW_ASTERISK_in_describeQuery216 = new BitSet(
            new long[] { 0x0000000003468000L });
    public static final BitSet FOLLOW_datasetClause_in_describeQuery220 = new BitSet(
            new long[] { 0x0000000003468000L });
    public static final BitSet FOLLOW_whereClause_in_describeQuery223 = new BitSet(
            new long[] { 0x0000000001440000L });
    public static final BitSet FOLLOW_solutionModifier_in_describeQuery226 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_ASK_in_askQuery243 = new BitSet(
            new long[] { 0x0000000002028000L });
    public static final BitSet FOLLOW_datasetClause_in_askQuery245 = new BitSet(
            new long[] { 0x0000000002028000L });
    public static final BitSet FOLLOW_whereClause_in_askQuery248 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_FROM_in_datasetClause265 = new BitSet(new long[] {
            0x00000180000100A0L, 0x0000000000010000L });
    public static final BitSet FOLLOW_defaultGraphClause_in_datasetClause269 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_namedGraphClause_in_datasetClause273 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_sourceSelector_in_defaultGraphClause292 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_NAMED_in_namedGraphClause309 = new BitSet(new long[] {
            0x00000180000000A0L, 0x0000000000010000L });
    public static final BitSet FOLLOW_sourceSelector_in_namedGraphClause311 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_iriRef_in_sourceSelector328 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_WHERE_in_whereClause345 = new BitSet(
            new long[] { 0x0000000002028000L });
    public static final BitSet FOLLOW_groupGraphPattern_in_whereClause348 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_orderClause_in_solutionModifier365 = new BitSet(
            new long[] { 0x0000000001400002L });
    public static final BitSet FOLLOW_limitOffsetClauses_in_solutionModifier368 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_limitClause_in_limitOffsetClauses388 = new BitSet(
            new long[] { 0x0000000001400002L });
    public static final BitSet FOLLOW_offsetClause_in_limitOffsetClauses390 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_offsetClause_in_limitOffsetClauses395 = new BitSet(
            new long[] { 0x0000000000400002L });
    public static final BitSet FOLLOW_limitClause_in_limitOffsetClauses397 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_ORDER_in_orderClause417 = new BitSet(
            new long[] { 0x0000000000080000L });
    public static final BitSet FOLLOW_BY_in_orderClause419 = new BitSet(new long[] {
            0xFFE00181003000A0L, 0x0000000000010000L });
    public static final BitSet FOLLOW_orderCondition_in_orderClause421 = new BitSet(new long[] {
            0xFFE00181003000A2L, 0x0000000000010000L });
    public static final BitSet FOLLOW_set_in_orderCondition441 = new BitSet(
            new long[] { 0x0000000100000000L });
    public static final BitSet FOLLOW_brackettedExpression_in_orderCondition451 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_constraint_in_orderCondition463 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_var_in_orderCondition467 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_LIMIT_in_limitClause486 = new BitSet(
            new long[] { 0x0000000000800000L });
    public static final BitSet FOLLOW_INTEGER_in_limitClause488 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_OFFSET_in_offsetClause505 = new BitSet(
            new long[] { 0x0000000000800000L });
    public static final BitSet FOLLOW_INTEGER_in_offsetClause507 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_OPEN_CURLY_BRACE_in_groupGraphPattern524 = new BitSet(
            new long[] { 0x000001A1BA8280A0L, 0x000000000003FFFCL });
    public static final BitSet FOLLOW_triplesBlock_in_groupGraphPattern526 = new BitSet(
            new long[] { 0x00000000BA028000L });
    public static final BitSet FOLLOW_graphPatternNotTriples_in_groupGraphPattern533 = new BitSet(
            new long[] { 0x000001A1BE8280A0L, 0x000000000003FFFCL });
    public static final BitSet FOLLOW_filter_in_groupGraphPattern537 = new BitSet(new long[] {
            0x000001A1BE8280A0L, 0x000000000003FFFCL });
    public static final BitSet FOLLOW_DOT_in_groupGraphPattern541 = new BitSet(new long[] {
            0x000001A1BA8280A0L, 0x000000000003FFFCL });
    public static final BitSet FOLLOW_triplesBlock_in_groupGraphPattern544 = new BitSet(
            new long[] { 0x00000000BA028000L });
    public static final BitSet FOLLOW_CLOSE_CURLY_BRACE_in_groupGraphPattern550 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_triplesSameSubject_in_triplesBlock567 = new BitSet(
            new long[] { 0x0000000004000002L });
    public static final BitSet FOLLOW_DOT_in_triplesBlock571 = new BitSet(new long[] {
            0x000001A1008000A2L, 0x000000000003FFFCL });
    public static final BitSet FOLLOW_triplesBlock_in_triplesBlock573 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_optionalGraphPattern_in_graphPatternNotTriples594 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_groupOrUnionGraphPattern_in_graphPatternNotTriples598 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_graphGraphPattern_in_graphPatternNotTriples602 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_OPTIONAL_in_optionalGraphPattern619 = new BitSet(
            new long[] { 0x0000000002028000L });
    public static final BitSet FOLLOW_groupGraphPattern_in_optionalGraphPattern621 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_GRAPH_in_graphGraphPattern638 = new BitSet(new long[] {
            0x00000180000000A0L, 0x0000000000010000L });
    public static final BitSet FOLLOW_varOrIRIref_in_graphGraphPattern640 = new BitSet(
            new long[] { 0x0000000002028000L });
    public static final BitSet FOLLOW_groupGraphPattern_in_graphGraphPattern642 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_groupGraphPattern_in_groupOrUnionGraphPattern659 = new BitSet(
            new long[] { 0x0000000040000002L });
    public static final BitSet FOLLOW_UNION_in_groupOrUnionGraphPattern663 = new BitSet(
            new long[] { 0x0000000002028000L });
    public static final BitSet FOLLOW_groupGraphPattern_in_groupOrUnionGraphPattern665 = new BitSet(
            new long[] { 0x0000000040000002L });
    public static final BitSet FOLLOW_FILTER_in_filter685 = new BitSet(new long[] {
            0xFFE00181000000A0L, 0x0000000000010000L });
    public static final BitSet FOLLOW_constraint_in_filter687 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_brackettedExpression_in_constraint704 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_builtInCall_in_constraint708 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_functionCall_in_constraint712 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_iriRef_in_functionCall729 = new BitSet(
            new long[] { 0x0000000100000000L });
    public static final BitSet FOLLOW_argList_in_functionCall731 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_OPEN_BRACE_in_argList750 = new BitSet(
            new long[] { 0x0000000200000000L });
    public static final BitSet FOLLOW_CLOSE_BRACE_in_argList752 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_OPEN_BRACE_in_argList756 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_expression_in_argList758 = new BitSet(
            new long[] { 0x0000000600000000L });
    public static final BitSet FOLLOW_COMMA_in_argList762 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_expression_in_argList764 = new BitSet(
            new long[] { 0x0000000600000000L });
    public static final BitSet FOLLOW_CLOSE_BRACE_in_argList769 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_OPEN_CURLY_BRACE_in_constructTemplate788 = new BitSet(
            new long[] { 0x000001A1088000A0L, 0x000000000003FFFCL });
    public static final BitSet FOLLOW_constructTriples_in_constructTemplate790 = new BitSet(
            new long[] { 0x0000000008000000L });
    public static final BitSet FOLLOW_CLOSE_CURLY_BRACE_in_constructTemplate793 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_triplesSameSubject_in_constructTriples810 = new BitSet(
            new long[] { 0x0000000004000002L });
    public static final BitSet FOLLOW_DOT_in_constructTriples814 = new BitSet(new long[] {
            0x000001A1008000A2L, 0x000000000003FFFCL });
    public static final BitSet FOLLOW_constructTriples_in_constructTriples816 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_varOrTerm_in_triplesSameSubject837 = new BitSet(new long[] {
            0x00000190000000A0L, 0x0000000000010000L });
    public static final BitSet FOLLOW_propertyListNotEmpty_in_triplesSameSubject839 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_triplesNode_in_triplesSameSubject843 = new BitSet(new long[] {
            0x00000190000000A0L, 0x0000000000010000L });
    public static final BitSet FOLLOW_propertyList_in_triplesSameSubject845 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_verb_in_propertyListNotEmpty862 = new BitSet(new long[] {
            0x000001A1008000A0L, 0x000000000003FFFCL });
    public static final BitSet FOLLOW_objectList_in_propertyListNotEmpty864 = new BitSet(
            new long[] { 0x0000000800000002L });
    public static final BitSet FOLLOW_SEMICOLON_in_propertyListNotEmpty868 = new BitSet(new long[] {
            0x00000198000000A2L, 0x0000000000010000L });
    public static final BitSet FOLLOW_verb_in_propertyListNotEmpty872 = new BitSet(new long[] {
            0x000001A1008000A0L, 0x000000000003FFFCL });
    public static final BitSet FOLLOW_objectList_in_propertyListNotEmpty874 = new BitSet(
            new long[] { 0x0000000800000002L });
    public static final BitSet FOLLOW_propertyListNotEmpty_in_propertyList897 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_object_in_objectList915 = new BitSet(
            new long[] { 0x0000000400000002L });
    public static final BitSet FOLLOW_COMMA_in_objectList919 = new BitSet(new long[] {
            0x000001A1008000A0L, 0x000000000003FFFCL });
    public static final BitSet FOLLOW_object_in_objectList921 = new BitSet(
            new long[] { 0x0000000400000002L });
    public static final BitSet FOLLOW_graphNode_in_object941 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_varOrIRIref_in_verb958 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_A_in_verb966 = new BitSet(new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_collection_in_triplesNode983 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_blankNodePropertyList_in_triplesNode991 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_OPEN_SQUARE_BRACE_in_blankNodePropertyList1008 = new BitSet(
            new long[] { 0x00000190000000A0L, 0x0000000000010000L });
    public static final BitSet FOLLOW_propertyListNotEmpty_in_blankNodePropertyList1010 = new BitSet(
            new long[] { 0x0000004000000000L });
    public static final BitSet FOLLOW_CLOSE_SQUARE_BRACE_in_blankNodePropertyList1012 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_OPEN_BRACE_in_collection1029 = new BitSet(new long[] {
            0x000001A1008000A0L, 0x000000000003FFFCL });
    public static final BitSet FOLLOW_graphNode_in_collection1031 = new BitSet(new long[] {
            0x000001A3008000A0L, 0x000000000003FFFCL });
    public static final BitSet FOLLOW_CLOSE_BRACE_in_collection1034 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_varOrTerm_in_graphNode1051 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_triplesNode_in_graphNode1055 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_var_in_varOrTerm1072 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_graphTerm_in_varOrTerm1080 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_var_in_varOrIRIref1097 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_iriRef_in_varOrIRIref1101 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_set_in_var0 = new BitSet(new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_iriRef_in_graphTerm1143 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_rdfLiteral_in_graphTerm1151 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_numericLiteral_in_graphTerm1159 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_booleanLiteral_in_graphTerm1167 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_blankNode_in_graphTerm1175 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_OPEN_BRACE_in_graphTerm1183 = new BitSet(
            new long[] { 0x0000000200000000L });
    public static final BitSet FOLLOW_CLOSE_BRACE_in_graphTerm1185 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_conditionalOrExpression_in_expression1202 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression1219 = new BitSet(
            new long[] { 0x0000020000000002L });
    public static final BitSet FOLLOW_OR_in_conditionalOrExpression1223 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_conditionalAndExpression_in_conditionalOrExpression1225 = new BitSet(
            new long[] { 0x0000020000000002L });
    public static final BitSet FOLLOW_valueLogical_in_conditionalAndExpression1245 = new BitSet(
            new long[] { 0x0000040000000002L });
    public static final BitSet FOLLOW_AND_in_conditionalAndExpression1249 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_valueLogical_in_conditionalAndExpression1251 = new BitSet(
            new long[] { 0x0000040000000002L });
    public static final BitSet FOLLOW_relationalExpression_in_valueLogical1271 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression1288 = new BitSet(
            new long[] { 0x0001F80000000002L });
    public static final BitSet FOLLOW_EQUAL_in_relationalExpression1292 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression1294 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_NOT_EQUAL_in_relationalExpression1298 = new BitSet(
            new long[] { 0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression1300 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_LESS_in_relationalExpression1304 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression1306 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_GREATER_in_relationalExpression1310 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression1312 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_LESS_EQUAL_in_relationalExpression1316 = new BitSet(
            new long[] { 0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression1318 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_GREATER_EQUAL_in_relationalExpression1322 = new BitSet(
            new long[] { 0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_numericExpression_in_relationalExpression1324 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_additiveExpression_in_numericExpression1344 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression1361 = new BitSet(
            new long[] { 0x0006000000800002L, 0x00000000000003FCL });
    public static final BitSet FOLLOW_PLUS_in_additiveExpression1365 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression1367 = new BitSet(
            new long[] { 0x0006000000800002L, 0x00000000000003FCL });
    public static final BitSet FOLLOW_MINUS_in_additiveExpression1371 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression1373 = new BitSet(
            new long[] { 0x0006000000800002L, 0x00000000000003FCL });
    public static final BitSet FOLLOW_numericLiteralPositive_in_additiveExpression1377 = new BitSet(
            new long[] { 0x0006000000800002L, 0x00000000000003FCL });
    public static final BitSet FOLLOW_numericLiteralNegative_in_additiveExpression1381 = new BitSet(
            new long[] { 0x0006000000800002L, 0x00000000000003FCL });
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression1401 = new BitSet(
            new long[] { 0x0008000000000802L });
    public static final BitSet FOLLOW_ASTERISK_in_multiplicativeExpression1405 = new BitSet(
            new long[] { 0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression1407 = new BitSet(
            new long[] { 0x0008000000000802L });
    public static final BitSet FOLLOW_DIVIDE_in_multiplicativeExpression1411 = new BitSet(
            new long[] { 0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression1413 = new BitSet(
            new long[] { 0x0008000000000802L });
    public static final BitSet FOLLOW_NOT_in_unaryExpression1433 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_primaryExpression_in_unaryExpression1435 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_PLUS_in_unaryExpression1443 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_primaryExpression_in_unaryExpression1445 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_MINUS_in_unaryExpression1453 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_primaryExpression_in_unaryExpression1455 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_primaryExpression_in_unaryExpression1463 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_brackettedExpression_in_primaryExpression1480 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_builtInCall_in_primaryExpression1484 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_iriRefOrFunction_in_primaryExpression1488 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_rdfLiteral_in_primaryExpression1492 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_numericLiteral_in_primaryExpression1496 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_booleanLiteral_in_primaryExpression1500 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_var_in_primaryExpression1504 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_OPEN_BRACE_in_brackettedExpression1521 = new BitSet(
            new long[] { 0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_expression_in_brackettedExpression1523 = new BitSet(
            new long[] { 0x0000000200000000L });
    public static final BitSet FOLLOW_CLOSE_BRACE_in_brackettedExpression1525 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_STR_in_builtInCall1542 = new BitSet(
            new long[] { 0x0000000100000000L });
    public static final BitSet FOLLOW_OPEN_BRACE_in_builtInCall1544 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_expression_in_builtInCall1546 = new BitSet(
            new long[] { 0x0000000200000000L });
    public static final BitSet FOLLOW_CLOSE_BRACE_in_builtInCall1548 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_LANG_in_builtInCall1556 = new BitSet(
            new long[] { 0x0000000100000000L });
    public static final BitSet FOLLOW_OPEN_BRACE_in_builtInCall1558 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_expression_in_builtInCall1560 = new BitSet(
            new long[] { 0x0000000200000000L });
    public static final BitSet FOLLOW_CLOSE_BRACE_in_builtInCall1562 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_LANGMATCHES_in_builtInCall1570 = new BitSet(
            new long[] { 0x0000000100000000L });
    public static final BitSet FOLLOW_OPEN_BRACE_in_builtInCall1572 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_expression_in_builtInCall1574 = new BitSet(
            new long[] { 0x0000000400000000L });
    public static final BitSet FOLLOW_COMMA_in_builtInCall1576 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_expression_in_builtInCall1578 = new BitSet(
            new long[] { 0x0000000200000000L });
    public static final BitSet FOLLOW_CLOSE_BRACE_in_builtInCall1580 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_DATATYPE_in_builtInCall1588 = new BitSet(
            new long[] { 0x0000000100000000L });
    public static final BitSet FOLLOW_OPEN_BRACE_in_builtInCall1590 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_expression_in_builtInCall1592 = new BitSet(
            new long[] { 0x0000000200000000L });
    public static final BitSet FOLLOW_CLOSE_BRACE_in_builtInCall1594 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_BOUND_in_builtInCall1602 = new BitSet(
            new long[] { 0x0000000100000000L });
    public static final BitSet FOLLOW_OPEN_BRACE_in_builtInCall1604 = new BitSet(
            new long[] { 0x0000018000000000L });
    public static final BitSet FOLLOW_var_in_builtInCall1606 = new BitSet(
            new long[] { 0x0000000200000000L });
    public static final BitSet FOLLOW_CLOSE_BRACE_in_builtInCall1608 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_SAMETERM_in_builtInCall1616 = new BitSet(
            new long[] { 0x0000000100000000L });
    public static final BitSet FOLLOW_OPEN_BRACE_in_builtInCall1618 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_expression_in_builtInCall1620 = new BitSet(
            new long[] { 0x0000000400000000L });
    public static final BitSet FOLLOW_COMMA_in_builtInCall1622 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_expression_in_builtInCall1624 = new BitSet(
            new long[] { 0x0000000200000000L });
    public static final BitSet FOLLOW_CLOSE_BRACE_in_builtInCall1626 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_ISIRI_in_builtInCall1634 = new BitSet(
            new long[] { 0x0000000100000000L });
    public static final BitSet FOLLOW_OPEN_BRACE_in_builtInCall1636 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_expression_in_builtInCall1638 = new BitSet(
            new long[] { 0x0000000200000000L });
    public static final BitSet FOLLOW_CLOSE_BRACE_in_builtInCall1640 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_ISURI_in_builtInCall1648 = new BitSet(
            new long[] { 0x0000000100000000L });
    public static final BitSet FOLLOW_OPEN_BRACE_in_builtInCall1650 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_expression_in_builtInCall1652 = new BitSet(
            new long[] { 0x0000000200000000L });
    public static final BitSet FOLLOW_CLOSE_BRACE_in_builtInCall1654 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_ISBLANK_in_builtInCall1662 = new BitSet(
            new long[] { 0x0000000100000000L });
    public static final BitSet FOLLOW_OPEN_BRACE_in_builtInCall1664 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_expression_in_builtInCall1666 = new BitSet(
            new long[] { 0x0000000200000000L });
    public static final BitSet FOLLOW_CLOSE_BRACE_in_builtInCall1668 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_ISLITERAL_in_builtInCall1676 = new BitSet(
            new long[] { 0x0000000100000000L });
    public static final BitSet FOLLOW_OPEN_BRACE_in_builtInCall1678 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_expression_in_builtInCall1680 = new BitSet(
            new long[] { 0x0000000200000000L });
    public static final BitSet FOLLOW_CLOSE_BRACE_in_builtInCall1682 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_regexExpression_in_builtInCall1690 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_REGEX_in_regexExpression1707 = new BitSet(
            new long[] { 0x0000000100000000L });
    public static final BitSet FOLLOW_OPEN_BRACE_in_regexExpression1709 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_expression_in_regexExpression1711 = new BitSet(
            new long[] { 0x0000000400000000L });
    public static final BitSet FOLLOW_COMMA_in_regexExpression1713 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_expression_in_regexExpression1715 = new BitSet(
            new long[] { 0x0000000600000000L });
    public static final BitSet FOLLOW_COMMA_in_regexExpression1719 = new BitSet(new long[] {
            0xFFF60181008000A0L, 0x000000000001FFFCL });
    public static final BitSet FOLLOW_expression_in_regexExpression1721 = new BitSet(
            new long[] { 0x0000000200000000L });
    public static final BitSet FOLLOW_CLOSE_BRACE_in_regexExpression1726 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_iriRef_in_iriRefOrFunction1743 = new BitSet(
            new long[] { 0x0000000100000002L });
    public static final BitSet FOLLOW_argList_in_iriRefOrFunction1745 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_string_in_rdfLiteral1763 = new BitSet(new long[] {
            0x0000000000000002L, 0x0000000000000003L });
    public static final BitSet FOLLOW_LANGTAG_in_rdfLiteral1767 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_REFERENCE_in_rdfLiteral1773 = new BitSet(new long[] {
            0x00000180000000A0L, 0x0000000000010000L });
    public static final BitSet FOLLOW_iriRef_in_rdfLiteral1775 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_numericLiteralUnsigned_in_numericLiteral1797 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_numericLiteralPositive_in_numericLiteral1801 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_numericLiteralNegative_in_numericLiteral1805 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_set_in_numericLiteralUnsigned0 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_set_in_numericLiteralPositive0 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_set_in_numericLiteralNegative0 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_set_in_booleanLiteral0 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_set_in_string0 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_IRI_REF_in_iriRef1987 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_prefixedName_in_iriRef1995 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_set_in_prefixedName0 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_BLANK_NODE_LABEL_in_blankNode2037 = new BitSet(
            new long[] { 0x0000000000000002L });
    public static final BitSet FOLLOW_OPEN_SQUARE_BRACE_in_blankNode2045 = new BitSet(
            new long[] { 0x0000004000000000L });
    public static final BitSet FOLLOW_CLOSE_SQUARE_BRACE_in_blankNode2047 = new BitSet(
            new long[] { 0x0000000000000002L });

}