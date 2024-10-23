package mo.organization;

public interface StageAction {
    String getName();
    void init(ProjectOrganization organization, Participant participant, StageModule stage);
}
