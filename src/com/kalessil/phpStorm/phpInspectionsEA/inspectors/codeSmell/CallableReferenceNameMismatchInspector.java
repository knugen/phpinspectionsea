package com.kalessil.phpStorm.phpInspectionsEA.inspectors.codeSmell;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.psi.elements.Function;
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.kalessil.phpStorm.phpInspectionsEA.openApi.BasePhpElementVisitor;
import com.kalessil.phpStorm.phpInspectionsEA.openApi.BasePhpInspection;
import org.jetbrains.annotations.NotNull;

public class CallableReferenceNameMismatchInspector extends BasePhpInspection {
    private static final String strProblemDescription = "Name provided in this call shall be %n% (case mismatch)";

    @NotNull
    public String getShortName() {
        return "CallableReferenceNameMismatchInspection";
    }

    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new BasePhpElementVisitor() {
            public void visitPhpMethodReference(MethodReference reference) {
                inspectCaseIdentity(reference);
            }
            public void visitPhpFunctionCall(FunctionReference reference) {
                inspectCaseIdentity(reference);
            }

            private void inspectCaseIdentity(FunctionReference reference) {
                String strNameGiven = reference.getName();
                if (!StringUtil.isEmpty(strNameGiven)) {
                    /* resolve callable */
                    PsiElement callable = reference.resolve();
                    if (callable instanceof Function) {
                        String strNameDefined = ((Function) callable).getName();
                        /* ensure case is matches */
                        if (!StringUtil.isEmpty(strNameDefined) && !strNameDefined.equals(strNameGiven)) {
                            /* report issues found */
                            String strMessage = strProblemDescription.replace("%n%", strNameDefined);
                            holder.registerProblem(reference, strMessage, ProblemHighlightType.WEAK_WARNING);
                        }
                    }
                }
            }
        };
    }
}
