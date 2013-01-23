Ext.define('org.osehra.hmp.PopUpButton', {
    extend:'Ext.button.Button',
    alias:'widget.popupbutton',
    ui:'popup',
    arrowCls:'',
    shadow:'sides',
    closeButtonText:'',
    listeners:{
        menushow:function (btn, menu) {
            if (btn.getWidth() > menu.getWidth()) {
                menu.setWidth(btn.getWidth());
            }
            if (btn.popUp.loader) {
                menu.getComponent(0).getLoader().load();
            }
            btn.showShadow();
        },
        menuhide:function (btn, menu) {
            btn.hideShadow();
        }
    },
    // private
    initComponent:function () {
        var me = this;

        /**
         * @cfg {Ext.Component/String/Object} popUp
         * Convenience config.
         *
         *      popUp: {
         *          xtype: 'component',
         *          html: '<div>foo</div>'
         *      }
         *
         * is equivalent to
         *
         *      menu: {
         *          items: [
         *              {
         *                  xtype: 'component',
         *                  html: '<div>foo</div>'
         *              }
         *          ]
         *      }
         */
        if (Ext.isDefined(me.popUp)) {
            if (Ext.isString(me.popUp.tpl) || Ext.isArray(me.popUp.tpl)) {
                me.popUp.tpl = Ext.create('Ext.XTemplate', me.popUp.tpl);
            }
            me.menu = {
                componentCls:'hmp-popupbutton-menu',
                plain:true,
                items:[
                    me.popUp
                ],
                fbar:[
                    '->',
                    {
                        xtype:'button',
                        text:'Close',
                        handler:function (btn) {
                            me.hideMenu();
                        }
                    }
                ]
            };
        }
        /**
         * @cfg {Ext.Component/String/Object} popUpButtons
         * Convenience config used for adding buttons docked to the bottom of the panel.
         *
         *      popUpButtons: [
         *          { text: 'Button 1' }
         *      ]
         *
         * is equivalent to
         *
         *      menu: {
         *          fbar: [
         *              { text: 'Button 1' }
         *          ]
         *      }
         */
        if (Ext.isDefined(me.popUpButtons) && Ext.isDefined(me.menu)) {
            me.menu.fbar = me.popUpButtons;
        }

        if (Ext.isString(me.tpl) || Ext.isArray(me.tpl)) {
            me.tpl = Ext.create('Ext.XTemplate', me.tpl);
        }

        me.callParent(arguments);

        if (Ext.isDefined(me.popUp)) {
            var popUp = me.menu.getComponent(0);
            me.mon(popUp, 'afterrender', me.onAfterPopUpRender, me);
        }
    },
    /**
     * Update the content area of a component.
     * @param {String/Object} htmlOrData If this component has been configured with a template via the tpl config then
     * it will use this argument as data to populate the template. If this component was not configured with a template,
     * the components content area will be updated via Ext.Element update.  If the popUp component has been configured
     * with a template via its tpl config then the popUp content area will use this argument to populate the popUp
     * component's template.
     * @param {Boolean} [loadScripts=false] Only legitimate when using the html configuration.
     * @param {Function} [callback] Only legitimate when using the html configuration. Callback to execute when
     * scripts have finished loading
     *
     * @since Ext 3
     */
    update:function (htmlOrData, loadScripts, cb) {
        var me = this;

        if (me.popUp.tpl && !Ext.isString(htmlOrData)) {
            if (me.menu.rendered) {
                me.menu.getComponent(0).update(htmlOrData);
            }
        }

        me.callParent(arguments);
    },
    /**
     * @private
     */
    onAfterPopUpRender:function () {
        this.menu.getComponent(0).update(this.data);
    },
    /**
     * Shows this button's menu (if it has one)
     *
     * overridden in order to offset menu up one pixel to cover button's bottom border
     */
    showMenu:function () {
        var me = this;
        if (me.rendered && me.menu) {
            if (me.tooltip && me.getTipAttr() != 'title') {
                Ext.tip.QuickTipManager.getQuickTip().cancelShow(me.btnEl);
            }
            if (me.menu.isVisible()) {
                me.menu.hide();
            }

            me.menu.showBy(me.el, me.menuAlign, [0, -1]);
        }
        return me;
    },
    showShadow:function () {
        var me = this;
        me.shadow = Ext.create("Ext.Shadow", {
            mode:'frame'
        });
        me.shadow.show(me.getEl());
    },
    hideShadow:function () {
        if (this.shadow) this.shadow.hide();
    },
    onMenuShow:function () {
        this.callParent(arguments);

        this.restoreStyles = this.getEl().getStyle([
            "border-top-color",
            "border-left-color",
            "border-right-color",
            "border-bottom-left-radius",
            "border-bottom-right-radius"
        ]);

        var menuZIndex = this.menu.getEl().getZIndex();
        var menuBorderColor = this.menu.getEl().getStyle("border-color");
        var styles = {
            "z-index":menuZIndex + 1,
            "border-top-color":menuBorderColor,
            "border-left-color":menuBorderColor,
            "border-right-color":menuBorderColor,
            "border-bottom-left-radius":"0px",
            "border-bottom-right-radius":"0px"
        };
//        var menuZIndex = this.menu.el.getZIndex();
        this.getEl().setStyle(styles);
//        this.getEl().setStyle("z-index", menuZIndex + 1);
    },
    onMenuHide:function () {
        this.callParent(arguments);

        Ext.apply(this.restoreStyles, {"z-index":""});
        this.getEl().setStyle(this.restoreStyles);
    }
});
