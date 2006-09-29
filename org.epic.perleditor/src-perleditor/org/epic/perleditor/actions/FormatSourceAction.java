package org.epic.perleditor.actions;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.epic.perleditor.PerlEditorPlugin;
import org.epic.perleditor.editors.PerlEditor;
import org.epic.perleditor.editors.PerlEditorActionIds;
import org.epic.perleditor.editors.util.SourceFormatter;
public class FormatSourceAction extends PerlEditorAction
{
    public FormatSourceAction()
    {
    }
    public FormatSourceAction(PerlEditor editor)
    {
        super(editor);
    }
    
    public void run()
    {
        try
        {
            runImpl();
        }
        catch (CoreException e)
        {
            PerlEditorPlugin.getDefault().getLog().log(e.getStatus());
        }
    }
    private void runImpl() throws CoreException
    {
        PerlEditor editor = getEditor();
        IDocument document =
            editor.getDocumentProvider().getDocument(editor.getEditorInput());
        StringBuffer text = new StringBuffer();
        text.append(document.get());
        if (text.length() == 0) return;
        // create an Anchor
        String myLineSep = "";
        try
        {
            myLineSep = document.getLineDelimiter(0);
        }
        catch (BadLocationException e)
        {
            // nothing needs to be done, no LineSep
        }
        String posAnchor = "�ߧ�";
        String insertAnchor = "#" + posAnchor;
        while (text.indexOf(insertAnchor) >= 0)
        {
            insertAnchor += posAnchor;
        }
        ISourceViewer viewer = editor.getViewer();
        StyledText myTextWidget = viewer.getTextWidget();
        // get the point to insert the Anchor
        int lineOfScreen = myTextWidget.getLineAtOffset(
            myTextWidget.getCaretOffset());
        int insPos = 0;
        if (lineOfScreen > 0)
        {
            if (lineOfScreen == myTextWidget.getLineCount() - 1)
            {
                insPos = text.length();
            }
            else
            {
                insPos = myTextWidget.getOffsetAtLine(lineOfScreen + 1)
                    - myLineSep.length();
            }
            // insert the Anchor
            text.insert(insPos, insertAnchor);
            // rel. Pos on the Screen
            lineOfScreen -= myTextWidget.getTopIndex();
        }
        StringBuffer newText = new StringBuffer();
        String formatText = new SourceFormatter().doConversion(text + "");
        newText.append(formatText);
        if (formatText == null ||
            formatText.length() == 0 ||
            newText.equals(text) ||
            formatText.equals(insertAnchor))
        {
            // no news after formatting!
            return;
        }
        int newPosAnchor = 0;
        if (insPos > 0)
        {
            newPosAnchor = newText.indexOf(insertAnchor);
            // fix for [ 1077441 ] - LeO: Frankly said, don't know how this
            // could happen!
            if (newPosAnchor < 0)
            {
                newText.delete(0, newText.length());
                newText.append(new SourceFormatter().doConversion(document
                    .get()));
                newPosAnchor = 0;
            }
            else
            {
                newText.delete(
                    newPosAnchor,
                    newPosAnchor + insertAnchor.length());
            }
        }
        document.set(newText + "");
        // set the new Cursor pos at the beginning of the Line
        myTextWidget.setCaretOffset(myTextWidget.getOffsetAtLine(
            myTextWidget.getLineAtOffset(newPosAnchor)));
        myTextWidget.setTopIndex(
            myTextWidget.getLineAtOffset(newPosAnchor) - lineOfScreen);
    }
    protected String getPerlActionId()
    {
        return PerlEditorActionIds.FORMAT_SOURCE;
    }
}