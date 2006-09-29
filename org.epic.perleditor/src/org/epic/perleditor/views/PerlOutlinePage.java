package org.epic.perleditor.views;
import java.util.Iterator;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.epic.core.model.*;
public class PerlOutlinePage extends ContentOutlinePage
{
    private SourceFile source;
    
    /**
     * Subroutine in which the caret was during last call to updateSelection
     * We keep track of it to speed up outline synchronisations in the common
     * case (caret movements within a sub).
     */
    private Subroutine lastCaretSub;
    
    public PerlOutlinePage(SourceFile source)
    {
        this.source = source;
    }
    public void createControl(Composite parent)
    {
        super.createControl(parent);
        TreeViewer viewer = getTreeViewer();
        viewer.setContentProvider(new PerlOutlineContentProvider());
        viewer.setLabelProvider(new PerlOutlineLabelProvider());
        viewer.setInput(source);
        viewer.setSorter(new ViewerSorter());
        getTreeViewer().expandAll();
    }
    public void updateContent(SourceFile source)
    {
        lastCaretSub = null;
        if (!source.equals(this.source))
        {
            this.source = source;
            getTreeViewer().setInput(source);
        }
        updateViewer();
    }
    
    public void updateSelection(int caretLine)
    {
        // check lastCaretSub first to speed up things in the most common case
        if (lastCaretSub == null ||
            caretLine < lastCaretSub.getStartLine() ||
            caretLine > lastCaretSub.getEndLine())
        {
            lastCaretSub = null;
            for (Iterator i = source.getSubs(); i.hasNext();)
            {
                Subroutine sub = (Subroutine) i.next();
                if (caretLine >= sub.getStartLine() &&
                    caretLine <= sub.getEndLine())
                {
                    lastCaretSub = sub;
                    break;
                }
            }
        }
        if (lastCaretSub != null)
            setSelection(new StructuredSelection(lastCaretSub));
        else
            setSelection(StructuredSelection.EMPTY);
    }
    
    private void updateViewer()
    {
        getTreeViewer().getControl().getDisplay().asyncExec(new Runnable() {
            public void run() {
                getTreeViewer().refresh();
                getTreeViewer().expandToLevel(3);
            } });
    }
}