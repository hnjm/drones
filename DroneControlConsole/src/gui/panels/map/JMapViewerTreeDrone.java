package gui.panels.map;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import org.openstreetmap.gui.jmapviewer.AbstractLayer;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.LayerGroup;
import org.openstreetmap.gui.jmapviewer.MemoryTileCache;
import org.openstreetmap.gui.jmapviewer.checkBoxTree.CheckBoxNodePanel;
import org.openstreetmap.gui.jmapviewer.checkBoxTree.CheckBoxTree;
import org.openstreetmap.gui.jmapviewer.interfaces.MapObject;

public class JMapViewerTreeDrone extends JPanel{
    /** Serial Version UID */
    private static final long serialVersionUID = 3050203054402323972L;

    private JMapViewer map;
    private CheckBoxTree tree;
    private JPanel treePanel;
    private JSplitPane splitPane;
    private LinkedList<Layer> layers = new LinkedList<Layer>();

    public JMapViewerTreeDrone(String name){
        this(name, false);
    }
    public JMapViewerTreeDrone(String name, boolean treeVisible){
        super();
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        tree = new CheckBoxTree(name);
        treePanel = new JPanel();
        treePanel.setLayout(new BorderLayout());
        treePanel.add(tree, BorderLayout.CENTER);
        map = new JMapViewer(new MemoryTileCache(), 4);
        new DroneMapController(map);

        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(150);
        
        //Provide minimum sizes for the two components in the split pane
        Dimension minimumSize = new Dimension(100, 50);
        //tree.setMinimumSize(minimumSize);
        map.setMinimumSize(minimumSize);
        createRefresh();
        setLayout(new BorderLayout());
        setTreeVisible(treeVisible);
        tree.addNodeListener(new MouseAdapter() {
            @Override
			public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }
            @Override
			public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }
            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    e.getSource();
                    AbstractLayer layer = ((CheckBoxNodePanel)e.getComponent()).getData().getAbstractLayer();
                    if(layer!=null)
                    	JMapViewerTreeDrone.this.createPopupMenu(layer).show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }
    private JPopupMenu createPopupMenu(final AbstractLayer layer) {
        JMenuItem menuItemShow = new JMenuItem("show texts");
        JMenuItem menuItemHide = new JMenuItem("hide texts");
 
        //Create the popup menu.
        JPopupMenu popup = new JPopupMenu();
        
        // Create items
        if(layer.isVisibleTexts()==null){
            popup.add(menuItemShow);
            popup.add(menuItemHide);
        }else if(layer.isVisibleTexts()) popup.add(menuItemHide);
        else popup.add(menuItemShow);
        
        menuItemShow.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {
                setVisibleTexts(layer, true);
                if(layer.getParent()!=null) layer.getParent().calculateVisibleTexts();
//                map.repaint();
            }
        });
        menuItemHide.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {
                setVisibleTexts(layer, false);
                if(layer.getParent()!=null) layer.getParent().calculateVisibleTexts();
//                map.repaint();
            }
        });
 
        return popup;
    }
    private void setVisibleTexts(AbstractLayer layer, boolean visible){
        layer.setVisibleTexts(visible);
        if(layer instanceof LayerGroup){
            LayerGroup group = (LayerGroup)layer;
            if(group.getLayers()!=null) for(AbstractLayer al: group.getLayers()) setVisibleTexts(al, visible);
        }
    }
    public Layer addLayer(String name){
        Layer layer = new Layer(name);
        this.addLayer(layer);
        return layer;
    }
    
    public LinkedList<Layer> getLayers() {
		return layers;
	}
    
    public JMapViewerTreeDrone addLayer(Layer layer){
        tree.addLayer(layer);
        layers.add(layer);
        return this;
    }
    public JMapViewerTreeDrone addLayer(MapObject element){
        //element.getLayer().add(element);
        return addLayer(element.getLayer());
    }
    public Layer removeFromLayer(MapObject element){
        element.getLayer().getElements().remove(element);
        return element.getLayer();
    }
    public JMapViewer getViewer(){
        return map;
    }
    public CheckBoxTree getTree(){
        return tree;
    }
    public void addMapObject(MapObject o){
        
    }
    public void setTreeVisible(boolean visible){
        removeAll();
        revalidate();
        if(visible){
            splitPane.setLeftComponent(treePanel);
            splitPane.setRightComponent(map);
            add(splitPane, BorderLayout.CENTER);
        }else add(map, BorderLayout.CENTER);
//        repaint();
    }
    private void createRefresh(){
        tree.getModel().addTreeModelListener(new TreeModelListener() {
            @Override
            public void treeNodesChanged(final TreeModelEvent e) {
            }
            @Override
            public void treeNodesInserted(TreeModelEvent arg0) {
            }
            @Override
            public void treeNodesRemoved(TreeModelEvent arg0) {
            }
            @Override
            public void treeStructureChanged(TreeModelEvent arg0) {
            }
        });
    }
}
