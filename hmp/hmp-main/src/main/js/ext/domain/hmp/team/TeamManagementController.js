/**
 * Controls Team Management panel.
 */
Ext.define('org.osehra.hmp.team.TeamManagementController', {
    extend:'org.osehra.hmp.Controller',
    requires:[
        'org.osehra.hmp.team.TeamManagementPanel',
        'org.osehra.hmp.UserContext',
        'org.osehra.cpe.roster.RosterStore'
    ],
    refs:[
        {
            ref:'teamList',
            selector:'#teamList'
        },
        {
            ref:'teamForm',
            selector:'#teamEdit'
        },
        {
            ref:'teamName',
            selector:'#teamNameField'
        },
        {
            ref:'ownerName',
            selector:'#ownerNameField'
        },
        {
            ref:'patientList',
            selector:'#patientList'
        },
        {
            ref:'staffList',
            selector:'#staffList'
        },
        {
            ref:'personPicker',
            selector:'personpicker'
        }
    ],
    init:function () {
//        console.log(Ext.getClassName(this) + ".init()");
        var me = this;
        me.control({
            '#teamList':{
                selectionchange:me.onTeamSelectionChange
            },
            '#createTeamButton':{
                click:me.doNewTeam
            },
            '#setTeamPatientsButton':{
                select:me.onSelectPatientList
            },
            'staffeditor':{
                assignmentchange:me.onAssignmentChange,
                positionadd:me.onPositionAdd,
                positionremove:me.onPositionRemove
            },
            '#saveTeamButton':{
                click:me.saveCurrentTeam
            }
        });
    },
    onLaunch:function () {
        var me = this;
        me.getTeamName().getEl().on('dblclick', me.onClickTeamName, me);
    },
    onTeamSelectionChange:function (grid, selected) {
        if (this.currentTeam) {
            var modified = this.getStaffList().getStore().getModifiedRecords(),
                removed = this.getStaffList().getStore().getRemovedRecords();
            if (modified.length > 0 || removed.length > 0) {
                this.syncTeamWithEditor();
                this.saveCurrentTeam();
            }
        }

        if (!selected) {
            this.getTeamForm().hide();
        } else {
            var team = selected[0];
            this.getTeamForm().show();
            this.getTeamName().update(team.get('displayName'));
            this.getOwnerName().setValue(team.get('ownerName'));
            this.refreshPatientList(team);

            var staff = team.staff();
            var assignments = new Array();
            staff.each(function (assignment) {
                assignments.push(assignment);
            });
            this.getStaffList().getStore().loadRecords(assignments);

            this.currentTeam = team;
        }
    },
    doNewTeam:function () {
//        console.log("New Team Please!");
        // TODO: increment new team name based on how many teams have "New Team *" as their name
        var userInfo = org.osehra.hmp.UserContext.getUserInfo();
        var newTeamName = "New Team";
        if (this.getNewTeamNumber() > 0) {
            newTeamName += " " + this.getNewTeamNumber();
        }
        var newTeam = Ext.create('org.osehra.hmp.team.Team', {
            displayName:newTeamName,
            ownerUid:userInfo.uid,
            ownerName:userInfo.displayName
        });
        var teamStore = Ext.getStore('teams');
        teamStore.add(newTeam);
        this.getTeamList().getSelectionModel().select(newTeam);
    },
    onClickTeamName:function () {
//        console.log("edit the team name!");
        if (!Ext.isDefined(this.teamNameEditor)) {
            var el = this.getTeamName().getEl();
            this.teamNameEditor = new Ext.create('Ext.Editor', {
                alignment:'tl-br',
                autoSize:true,
                offsets:el.getXY(), // need this to workaround default editor behavior for some reason
                updateEl:true, // update the innerHTML of the bound element when editing completes
                field:{
                    xtype:'textfield'
                }
            });
            this.teamNameEditor.on('complete', this.onEditTeamNameComplete, this);
        }
        this.teamNameEditor.startEdit(el);
    },
    onEditTeamNameComplete:function (editor, value, startValue) {
        var team = this.getCurrentTeam();
        if (!team) return;
        team.set('displayName', value);
    },
    onAssignmentChange:function (picker, assignment) {
//        console.log("assignment changed!");
        this.syncTeamWithEditor();
    },
    onPositionAdd:function (picker, position) {
//        console.log("add a position!");
        this.syncTeamWithEditor();
    },
    onPositionRemove:function (picker, position) {
//        console.log("remove a position!");
        this.syncTeamWithEditor();
    },
    /**
     * @private
     */
    syncTeamWithEditor:function () {
        var staff = this.currentTeam.staff();
        staff.suspendEvents();
        staff.removeAll();
        this.getStaffList().getStore().each(function (assignment) {
            staff.add(assignment);
        });
        this.getStaffList().getStore().commitChanges();
        staff.commitChanges();
        staff.resumeEvents();
    },
    getCurrentTeam:function () {
        return this.currentTeam;
    },
    /**
     * Returns the number to append to a new team given the number of existing new teams in the teams store.
     * <ul>
     *     <li>New Team</li>
     *     <li>New Team 1</li>
     *     <li>New Team 2</li>
     *     <li>New Team 3</li>
     * </ul>
     * @private
     */
    getNewTeamNumber:function () {
        var newTeams = Ext.getStore('teams').getNewRecords();
        return newTeams.length;
    },
    saveCurrentTeam:function () {
//        console.log('save it!');
        var team = this.currentTeam;
        var uid = team.get('uid');
        if (!uid) uid = 'new';
        var jsonData = team.data;
        jsonData.staff = new Array();
        team.staff().each(function (assignment) {
            jsonData.staff.push(assignment.data);
        });
        Ext.Ajax.request({
            url:'/teamMgmt/v1/team/' + uid,
            method:'POST',
            jsonData:jsonData,
            success:function (response) {
                var json = Ext.decode(response.responseText);
                team.set('uid', json.data.uid);
                team.commit();
            },
            failure:function () {

            }
        });
    },
    onSelectPatientList:function (combo, records) {
        var rosterId = combo.getValue();
        var team = this.getCurrentTeam();
        team.set('rosterId', rosterId);
//        console.log(rosterId);
//        var staffAssignment = Ext.create('org.osehra.hmp.team.TeamAssignment', {
//            positionUid:position.get('uid'),
//            positionName:position.get('name')
//        });
//        this.getStore().add(staffAssignment);
        combo.clearValue();
        this.refreshPatientList(team);
    },
    /**
     * @private
     */
    refreshPatientList:function(team) {
        this.getPatientList().setViewDef(this.getPatientList().viewID, {'roster.ien':team.get('rosterId')});
    }
});
