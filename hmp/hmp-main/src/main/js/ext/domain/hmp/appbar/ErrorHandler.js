Ext.require(['EXT.DOMAIN.hmp.appbar.ErrorManager']);
Ext.onReady(function () {
    // global exception handler: translates into a visual error
    Ext.Error.handle = function(err) {
        //Ext.util.Cookies.set('SUPPRESS_ERRORS', '1', new Date(2020,1,1));
        var SUPPRESS_ERRORS = Ext.util.Cookies.get('SUPPRESS_ERRORS');
        if (SUPPRESS_ERRORS && false) return;
        EXT.DOMAIN.hmp.appbar.ErrorManager.error('Browser/Ext Error.  <a href="">Please Reload</a>', 0, err.msg);

        // register the error in the appbar (so the diagnostic window can find it)
        console.log('ext error',err);
        EXT.DOMAIN.hmp.appbar.ErrorManager.errors.push({type: 'EXT', msg: err.msg});
        //return true;
    }

    // global AJAX request error handler: translates into visual error
    Ext.Ajax.mon('requestexception', function(conn, resp, opts) {
        var msg, details, url = resp.request.options.url;
        var errorHandledByView = opts.skipErrors||( opts.operation && opts.operation.skipErrors)||(opts.scope && opts.scope.skipErrors);
    	
        if (resp.status === 401) {
            // attempting a login, not an error
            if (url == '/j_spring_security_check' || url == '/auth/login') return;

            // unauthenticated.  Usually means the session timed out
            msg = 'Your session has expired! Please login again.';
            if(!errorHandledByView){
	            EXT.DOMAIN.hmp.appbar.ErrorManager.error(msg);
	            window.location.href = '/auth/login?msg=' + msg;
            }
        } else if (resp.status === 0) {
            // connection aborted (usually means the server is unresponsive)
        	// TODO: need to differentiate between browser aborted and timeout
            msg = 'Server/HTTP Request unresponsive or aborted. Please contact support.';
            debugger;
            if(!errorHandledByView){
            	EXT.DOMAIN.hmp.appbar.ErrorManager.error(msg, 0, url);
            }
            	
        } else if (resp.status === 500 || resp.status === 404) {
            // server error
            msg = 'HTTP Error ' + resp.status + ": " + resp.statusText;
            if(!errorHandledByView){
            	EXT.DOMAIN.hmp.appbar.ErrorManager.error('Server/HTTP Request Error.  Try again or <a href="">Reloading</a>', 0, url);
            }
            
            // if it was a JSON error response, try to parse it
            try {
            	var errorMsg = JSON.parse(resp.responseText);
            	if (errorMsg.success === false && errorMsg.error) {
            		details = errorMsg.error.message;
            	}
            } catch (err) {
            	// non jSON, ignore
            }
        } else if (resp.status === -1 && resp.aborted === true) {
        	// aborted really isn't an error, no need to display errorMsg
        	msg = 'HTTP request aborted';
        } else {
            // TODO:What else needs trapping?
            console.log('UNHANDLED HTTP STATUS: ', resp.status, resp);
        }

        // register the error in the appbar (so the diagnostic window can find it)
        EXT.DOMAIN.hmp.appbar.ErrorManager.errors.push({type: 'XHR', msg: msg, loc: url, details: details});
    });

    // Low level javascript error: attempt to translate into visual error
    window.onerror = function(msg, url, line) {
        //EXT.DOMAIN.hmp.appbar.ErrorManager.error('Browser Error.  You can try <a href="">Reloading</a>', 0, msg);

        // register the error in the appbar (so the diagnostic window can find it)
        EXT.DOMAIN.hmp.appbar.ErrorManager.errors.push({type: 'JS', msg: msg, loc: url + ' (line: ' + line + ')'});
    }
});
