package com.schematronQuickfix.escaliGuiComponents.lists;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.github.oxygenPlugins.common.gui.images.IconMap;
import com.github.oxygenPlugins.common.gui.lists.AbstractList;
import com.github.oxygenPlugins.common.gui.lists.items.AbstractItemMouseListener;
import com.github.oxygenPlugins.common.gui.swing.SwingUtil;
import com.schematronQuickfix.escali.control.report._QuickFix;
import com.schematronQuickfix.escali.control.report._Report;
import com.schematronQuickfix.escali.control.report._SVRLMessage;
import com.schematronQuickfix.escaliGuiComponents.adapter.EscaliMessangerAdapter;
import com.schematronQuickfix.escaliGuiComponents.lists.items.SVRLMessageListItem;
import com.schematronQuickfix.escaliGuiComponents.toolbar.ButtonFactory;
import com.schematronQuickfix.escaliGuiComponents.toolbar.ButtonFactory.OptionButton;
import com.schematronQuickfix.escaliGuiComponents.toolbar.ButtonFactory.PhaseButton;
import com.schematronQuickfix.escaliGuiComponents.toolbar.GlobalQuickFixToolbar;
import com.schematronQuickfix.escaliGuiComponents.toolbar.ValidationToolbar;

public class MessageList extends
		AbstractList<_SVRLMessage, SVRLMessageListItem> {

	private final EscaliMessangerAdapter ema;
	private PhaseButton phaseButton;
	private GridBagLayout gblEEP;

	private class MessageListener extends
			AbstractItemMouseListener<_SVRLMessage, SVRLMessageListItem> {

		public MessageListener(SVRLMessageListItem item) {
			super(item, MessageList.this);
		}

		@Override
		public void oneClick(MouseEvent e, boolean isSelected) {
			if(!isSelected) {
				ema.hideQuickFixAndUserEntryViewer();
			} else {
				ema.hideQuickFixAndUserEntryViewer();
				ema.viewQuickFixes(item.getModelNode(), item.getSelectedFix());
				ema.showMessage(this.item.getModelNode());
			}
		}

		@Override
		public void doubleClick(MouseEvent e) {
			ema.showMessage(this.item.getModelNode(), true);
		}
	}

	public MessageList(EscaliMessangerAdapter ema) { //, boolean standalone
		super();

		this.gblEEP = new GridBagLayout();
		this.emptyEndPanel.setLayout(gblEEP);
		
		this.isMultiSelectable = false;
		this.ema = ema;
		if (ema.isStandalon()) {
			this.addToolbar(new ValidationToolbar(ema));
		}
		int i = 0;
		for (JButton btn : GlobalQuickFixToolbar.getToolbarButtons(ema, this)) {
			this.addComponentToToolbar(btn, i++, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE);
		}
		this.addComponentToToolbar(new JPanel(), i++, 0, 1, 1, 1.0, 1.0, GridBagConstraints.EAST, GridBagConstraints.BOTH);
		this.phaseButton = ButtonFactory.getPhaseButton(ema, this);
		this.addComponentToToolbar(phaseButton.getButton(), i++, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE);
		OptionButton showOptionBtn = ButtonFactory.getOptionButton(ema, this);
		this.addComponentToToolbar(showOptionBtn.getButton(), i++, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE);
	}
	
	public void setEmptyLabel(JComponent label){
		SwingUtil.addComponent(this.emptyEndPanel, gblEEP, label, 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL);
	}

	public void viewReport(ArrayList<_Report> reports) {
		this.removeAllItems();
		this.phaseButton.implementReport(reports);
		for (_Report report : reports) {
			ArrayList<SVRLMessageListItem> itemList = new ArrayList<SVRLMessageListItem>();
			for (_SVRLMessage message : report.getMessages()) {
				SVRLMessageListItem msgItem = new SVRLMessageListItem(ema,
						message);
				msgItem.addSelectionListener(new MessageListener(msgItem));
				itemList.add(msgItem);
			}
			if(!report.hasIcon()){
				ImageIcon icon;
				switch (report.getMaxErrorLevelInt()) {
				case _SVRLMessage.LEVEL_FATAL_ERROR:
					icon = ema.getIcon(IconMap.MESSAGE_GROUP_FAILED);
					break;

				case _SVRLMessage.LEVEL_ERROR:
					icon = ema.getIcon(IconMap.MESSAGE_GROUP_FAILED);
					break;

				case _SVRLMessage.LEVEL_WARNING:
					icon = ema.getIcon(IconMap.MESSAGE_WARNING);
					break;

				case _SVRLMessage.LEVEL_INFO:
					icon = ema.getIcon(IconMap.MESSAGE_INFO);
					break;
				default:
					icon = ema.getIcon(IconMap.MESSAGE_GROUP_SUCCEDED);
					break;
				}
				report.setIcon(icon);
			}
			this.addListItemAsGroup(itemList, report);
		}
		updateUI();
	}

	public void viewReport(_Report report) {
		this.removeAllItems();
		this.phaseButton.implementReport(report);
		ArrayList<SVRLMessageListItem> itemList = new ArrayList<SVRLMessageListItem>();
		for (_SVRLMessage message : report.getMessages()) {
			SVRLMessageListItem msgItem = new SVRLMessageListItem(ema, message);
			msgItem.addSelectionListener(new MessageListener(msgItem));
			itemList.add(msgItem);
		}
		this.addListItemAsGroup(itemList, report);
		updateUI();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4210744459793445936L;

	public void notifySelectedFix(_QuickFix fix) {
		// TODO Auto-generated method stub
		SVRLMessageListItem item = this.getListItemByNode(fix.getParent());
		item.notifySelectedFix(fix);
	}

	public void notifyUnSelection(_SVRLMessage currentMessage) {

		SVRLMessageListItem item = this.getListItemByNode(currentMessage);
		item.notifyUnSelection();
	}

}