package hr.foi.air2003.menzapp.login.managers

import hr.foi.air2003.menzapp.core.ModulePresenter

interface ModuleListener {
    fun startModule(module: ModulePresenter)
}