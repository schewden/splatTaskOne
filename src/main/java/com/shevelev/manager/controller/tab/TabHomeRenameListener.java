package com.shevelev.manager.controller.tab;

import com.shevelev.manager.model.FileToDirectoryModel;
import com.shevelev.manager.view.DisplayUsers;
import com.shevelev.manager.view.PanelTree;
import com.shevelev.manager.view.menu.RenamePanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by denis on 20.11.17.
 */
public class TabHomeRenameListener implements ActionListener {

    private JFrame frame;
    private FileToDirectoryModel FileToDirectoryModel;
    private PanelTree panelTree;
    private DisplayUsers displayUsers;

    private boolean renameObject;
    private File currentSelectedFile;

    public TabHomeRenameListener(JFrame frame, FileToDirectoryModel FileToDirectoryModel,
                                 PanelTree panelTree,
                                 DisplayUsers displayUsers) {
        this.frame = frame;
        this.FileToDirectoryModel = FileToDirectoryModel;
        this.panelTree = panelTree;
        this.displayUsers = displayUsers;
    }

    public void actionPerformed(ActionEvent e) {
        currentSelectedFile = FileToDirectoryModel.getSelectedDirectory();
        if (currentSelectedFile != null) {
            RenamePanel renamePanel = new RenamePanel();
            UIManager.put("OptionPane.yesButtonText", "Переименовать");
            UIManager.put("OptionPane.noButtonText", "Отмена");

            int result = JOptionPane.showConfirmDialog(frame,
                    renamePanel.getRenamePanel(),
                    "Переименовать",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    if (!renamePanel.getName().getText().equals("")) {
                        File newFile = new File(currentSelectedFile.getParent(), renamePanel.getName().getText());
                        renameObject = currentSelectedFile.renameTo(newFile);
                        if (renameObject) {
                            if (newFile.isDirectory()) {
                                TreePath parentPath = panelTree.getTreePathInJTree(currentSelectedFile.getParentFile());
                                DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();

                                TreePath currentPath = panelTree.getTreePathInJTree(currentSelectedFile);
                                DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) currentPath.getLastPathComponent();

                                panelTree.getDefaultTreeModel().removeNodeFromParent(currentNode);

                                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newFile);
                                panelTree.getDefaultTreeModel().insertNodeInto(newNode, parentNode, parentNode.getChildCount());

                                FileToDirectoryModel.setFileToDirectory(FileToDirectoryModel.getFileToDirectory());
                                displayUsers.repaintGUI(FileToDirectoryModel.getListFilesAndDirectories());
                                FileToDirectoryModel.setSelectedDirectory(null);
                            } else {
                                FileToDirectoryModel.setFileToDirectory(FileToDirectoryModel.getFileToDirectory());
                                displayUsers.repaintGUI(FileToDirectoryModel.getListFilesAndDirectories());
                                FileToDirectoryModel.setSelectedDirectory(null);
                            }
                        }
                    } else {
                        String msg = "Вы не ввели имя файла.Пожалуйста,введите имя файла!";
                        ErrorMessage errorMessage = new ErrorMessage(frame);
                        errorMessage.errorMessagePane(msg, "Ошибка изменения имени файла");
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        } else {
            String msg = "Вы не выбрали файл.Пожалуйста, выберите файл и повторите действия!";
            ErrorMessage errorMessage = new ErrorMessage(frame);
            errorMessage.errorMessagePane(msg, "Ошибка изменения имени файла");
        }
    }
}
