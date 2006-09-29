package org.epic.perleditor.views;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.epic.core.model.SourceFile;
import org.epic.core.model.Subroutine;
public class PerlOutlinePage extends ContentOutlinePage
{
    private SourceFile source;
    private List prevContent;
    
    /**
     * Subroutine in which the caret was during last call to updateSelection
     * We keep track of it to speed up outline synchronisations in the common
     * case (caret movements within a sub).
     */
    private Subroutine lastCaretSub;
    
    public PerlOutlinePage(SourceFile source)
    {
        this.source = source;
        this.prevContent = new ArrayList();
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
        rememberContent(source);
    }
    public void updateContent(SourceFile source)
    {
        lastCaretSub = null;
        if (!source.equals(this.source))
        {
            this.source = source;
            getTreeViewer().setInput(source);
        }
        if (contentChanged(source))
        {
            updateViewer();
            rememberContent(source);
        }
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
    
    /**
     * @param source  SourceFile to be presented in the outline page
     * @return true if the outline page's content for <code>source</code>
     *         differs from the current content; false otherwise
     */
    private boolean contentChanged(SourceFile source)
    {
        Iterator j = prevContent.iterator();
        
        for (Iterator i = source.getSubs(); i.hasNext();)
        {
            if (!j.hasNext()) return true;
            
            Subroutine subI = (Subroutine) i.next();
            Subroutine subJ = (Subroutine) j.next();
            
            if (!subI.getName().equals(subJ.getName()) ||
                !subI.getParent().getName().equals(subJ.getParent().getName()))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Caches the content of the outline page derived from <code>source</code>.
     * This is necessary to avoid calling {@link #updateViewer} every time the
     * source file changes (yet the outline should stay unaffected).
     * 
     * @param source  SourceFile currently presented in the outline page
     */
    private void rememberContent(SourceFile source)
    {
        prevContent.clear();
        for (Iterator i = source.getSubs(); i.hasNext();)
            prevContent.add(i.next());
    }
    
    /**
     * Loads the current contents of the outline page into the tree viewer
     * and expands its nodes. This is an expensive operation, especially
     * under Windows where it results in a visible and annoying redrawing.
     */
    private void updateViewer()
    {
        getTreeViewer().getControl().getDisplay().asyncExec(new Runnable() {
            public void run() {
                getTreeViewer().refresh();
                getTreeViewer().expandToLevel(3);
            } });
    }
}