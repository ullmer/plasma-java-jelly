(jde-project-file-version "1.0")
(jde-set-variables
 '(jde-global-classpath (quote ("./build/classes" "./test/classes" "./src" "./test")))
 '(jde-gen-method-javadoc-comment "none")
 '(jde-javadoc-author-tag-template (quote ("* @author " user-login-name)))
 '(jde-junit4-test-class-template (quote ("(funcall jde-gen-boilerplate-function)" "(jde-gen-get-package-statement)" "\"import static org.junit.Assert.*;\" '>'n" "\"import org.junit.Test;\" '>'n" "'n" "(progn (require 'jde-javadoc) (jde-javadoc-insert-start-block))" "\" * \"" "\" Unit Test for class \"" "(jde-junit-get-testee-name (file-name-sans-extension (file-name-nondirectory buffer-file-name))) '>'n" "\" \" (jde-javadoc-insert-empty-line)" "\" \" (jde-javadoc-insert-empty-line)" "\" \" (jde-javadoc-insert 'tempo-template-jde-javadoc-author-tag)" "\" \" (jde-javadoc-insert 'tempo-template-jde-javadoc-end-block \"*/\")" "\"public class \"" "(file-name-sans-extension (file-name-nondirectory buffer-file-name))" "\" \" " "(if jde-gen-k&r " "()" "'>'n)" "\"{\"'>'n" "'n" "\"@Test\" '>'n" "\"public void testMethod() \" '>" "(if jde-gen-k&r " "() " "'>'n)" "\"{\"'>'n" "\"}\"'>'n'n" "\"}\">" "'>'n")))
 '(jde-sourcepath (quote ("./src" "./test")))
 '(jde-gen-interface-buffer-template (quote ("(funcall jde-gen-boilerplate-function)" "(jde-gen-get-package-statement)" "(progn (require 'jde-javadoc) (jde-javadoc-insert-start-block))" "\" \" (jde-javadoc-insert-empty-line)" "\" \" (jde-javadoc-insert-empty-line)" "\" \" (jde-gen-save-excursion (jde-javadoc-insert 'tempo-template-jde-javadoc-author-tag))" "\" \" (jde-javadoc-insert-end-block)" "\"public interface \"" "(file-name-sans-extension (file-name-nondirectory buffer-file-name))" "\" \" (jde-gen-get-extend-class)" "(jde-gen-electric-brace)" "'p'n" "\"}\">" "(if jde-gen-comments (concat \" // \"" "  (file-name-sans-extension (file-name-nondirectory buffer-file-name))))" "'>'n")))
 '(jde-gen-class-buffer-template (quote ("(funcall jde-gen-boilerplate-function)" "(jde-gen-get-package-statement)" "(when jde-gen-create-javadoc" "(progn (require 'jde-javadoc) (jde-javadoc-insert-start-block))" "'(l" "    \" \" (jde-javadoc-insert-empty-line)" "    \" \" (jde-gen-save-excursion (jde-javadoc-insert 'tempo-template-jde-javadoc-author-tag))" "    \" \" (jde-javadoc-insert-end-block)))" "\"public class \"" "(file-name-sans-extension (file-name-nondirectory buffer-file-name))" "\" \" (jde-gen-get-extend-class)" "(jde-gen-electric-brace)" "'p'n" "\"}\">" "(if jde-gen-comments (concat \" // \"" "  (file-name-sans-extension (file-name-nondirectory buffer-file-name))))" "'>'n" ";; Here comes the stuff that needs a fully generated class." ";; We jump back and add those things retrospectively." "(progn (tempo-backward-mark)" " (jde-gen-save-excursion" "  (jde-gen-get-interface-implementation t))" " (jde-gen-save-excursion" "  (jde-wiz-gen-method \"public\" \"\"" "   (file-name-sans-extension (file-name-nondirectory buffer-file-name)) \"\" \"\" \"\")))" ";; Move to constructor body. Set tempo-marks to nil in order to prevent tempo moving to mark." "(progn (re-search-forward \"^[ \\t]*$\") (setq tempo-marks nil) nil)")))
 '(jde-gen-create-javadoc t)
 '(jde-ant-working-directory ""))
