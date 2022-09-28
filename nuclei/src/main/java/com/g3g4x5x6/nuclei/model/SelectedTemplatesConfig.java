package com.g3g4x5x6.nuclei.model;


import java.util.LinkedList;
import java.util.List;

public class SelectedTemplatesConfig {
    private List<String> target = new LinkedList<>();
    private List<String> templates = new LinkedList<>();
    private List<String> workflows = new LinkedList<>();
    private List<String> proxy = new LinkedList<>();

    private String output;
    private String markdownExport;

    private boolean debug;


    public SelectedTemplatesConfig() {

    }

    public List<String> getTarget() {
        return target;
    }

    public void setTarget(List<String> target) {
        this.target = target;
    }

    public List<String> getTemplates() {
        return templates;
    }

    public void setTemplates(List<String> templates) {
        this.templates = templates;
    }

    public List<String> getWorkflows() {
        return workflows;
    }

    public void setWorkflows(List<String> workflows) {
        this.workflows = workflows;
    }

    public void addTemplate(String templatesPath) {
        templates.add(templatesPath);
    }

    public void addWorkflow(String workflowPath) {
        workflows.add(workflowPath);
    }

    public List<String> getProxy() {
        return proxy;
    }

    public void setProxy(List<String> proxy) {
        this.proxy = proxy;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getMarkdownExport() {
        return markdownExport;
    }

    public void setMarkdownExport(String markdownExport) {
        this.markdownExport = markdownExport;
    }
}
