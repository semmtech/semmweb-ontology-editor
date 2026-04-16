package tech.laces.ldp.api.message;


import tech.laces.ldp.api.message.content.LDPGroupInfo;
import tech.laces.ldp.api.message.content.LDPLicenceInfo;
import tech.laces.ldp.api.message.content.LDPPublicationCreateInfo;
import tech.laces.ldp.api.message.content.LDPPublicationInfo;
import tech.laces.ldp.api.message.content.LDPRepositoryInfo;


public class MessageExample {

    public final static LDPGroupInfo group = new LDPGroupInfo() {
        {
            description = "Group for academic purposes";
            name = "Academic";
            type = "PUBLIC";
            id = "67dbb5ab-c13c-4645-93b5-31b753ec49b7";
            pathSegment = "academic";
            path = "academic";
            createdBy = "admin";
            createdOn = 1639055208295L;
            modifiedBy = "admin";
            modifiedOn = 1750251494431L;
            owner = "admin";
            parentId = "";
        }
    };

    public final static LDPRepositoryInfo repository = new LDPRepositoryInfo() {
        {
            id = "26a0bac8-5c37-4c54-b3b1-1ad4551db061";
            name = "General Suggestions";
            description = "lorem ipsum";
            pathSegment = "general-suggestions";
            _public = false;
            owner = "Cruijff14";
            path = "stadion-improvements/general-suggestions";
            parentId = "67193215-bc14-43d9-a646-14ac95c089f6";
            createdBy = "Cruijff14";
            createdOn = 1673971427542L;
            modifiedBy = "Cruijff14";
            modifiedOn = 1673971427542L;
            role = "PUBLISHER";
        }
    };

    public final static LDPPublicationInfo publication = new LDPPublicationInfo() {
        {

            id = "67193215-bc14-43d9-a646-14ac95c089f6";
            name = "OTL Manager Schema";
            description = "string";
            repositoryId = "67193215-bc14-43d9-a646-14ac95c089f6";
            owner = "Semmtech";
            publisher = "Hannah Smith";
            publicationDate = 1687766202235L;
            uri = "/ns/semmtech/live/laces/schema/otl-manager/";
            sequenceId = "3fa85f64-5717-4562-b3fc-2c963f66afa6";
            sparqlEndpoint = "/ns/semmtech/live/laces/schema/otl-manager/";
            useVersionedBaseUri = false;
            versioningMode = "TIMESTAMP";
            licence = new LDPLicenceInfo() {
                {
                    name = "MIT";
                    url = "https://opensource.org/license/MIT";
                }
            };
            schemaURIs = new String[] { "http://www.laces.com" };
            pending = false;
            icon = "data:image/jpeg;base64,/9j/4R/+RXh...";
            ldvLink = "http://www.laces.com";
            _abstract = "Repellendus nobis nesciunt. Labore explicabo quidem tempore. Voluptatibus quam velit commodi.";
            contributor = "Hannah Smith";
            creator = "smithhannah";
        }
    };

    public final static LDPPublicationCreateInfo publicationCreate = new LDPPublicationCreateInfo() {
        {
            publicationUri = "/ns/semmtech/live/laces/schema/otl-manager/";
            _abstract = "Repellendus nobis nesciunt. Labore explicabo quidem tempore. Voluptatibus quam velit commodi.";
            addVersionToContent = true;
            schemaURIs = new String[] { "http://www.laces.com" };
            useVersionedBaseUri = true;
            name = "OTL Manager Schema";
            licence = new LDPLicenceInfo() {
                {
                    name = "MIT";
                    url = "https://opensource.org/license/MIT";
                }
            };
            creator = "smithhannah";
            versioningMode = "TIMESTAMP";
            owner = "Semmtech";
            ldvLink = "http://www.laces.com";
            icon = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAAF9SURBVHhe5ZDbTUQxDAVv/x1QGi3QBGiQslpCHs47tkean8uJWM/z7Zzn4/Mr/uaK3wCeI7wCeI3wJ4DHCP8CeIuQDOApQjaAlwjFAB4iVANYjyAKYDmCOIDVCE0BLEZoDmAtQlcASxG6A1iJMBTAQoThAKiZKQFQK9MCoEamBkBtTA+AmlgSALVQDfA8j1jJuxzxLrWN/56ylWqAlPyj+Fts2Ejo+eHQ++6d5QGwxMgRI28DWwJgjpEjRt4GtgXAFCNHjLwNLA2QMoVkk6Jlm2NpgPhbsIb0MOmuxJEAWENynGRT41gALCE5TrKpcTQA5pAcJ9nUOB4AY6SHSXcllgZIKdlJadnm6AqwyhNcFQB3c10A3MmVAXAX1wbAHVwdAFdzfQBciYoAuAo1AXAFqgLgbNQFwJmoDICzUBsAZ6A6AI6iPgCOYCIA9mImAPZgKgC2Yi4AtmAyAEoxGwAlmA6ANcwHwBIuAmAONwEwhasAGOMuALoPgO4DIPwAOMDZemgOIMwAAAAASUVORK5CYII=";
            contributor = "Hannah Smith";
            versionLabel = "Version 2.0";
            publisher = "Hannah Smith";
            description = "This publication contains an ontology of road signs used by the Dutch government";
        }
    };
}
