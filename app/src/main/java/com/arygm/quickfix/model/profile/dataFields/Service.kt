package com.arygm.quickfix.model.profile.dataFields

sealed class Service(val name : String)

class IncludedService(name : String) : Service(name)
class AddOnService(name : String) : Service(name)