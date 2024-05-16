//
//  HideNavigetion_ContentView.swift
//  learn_swiftUI
//
//  Created by 성준모 on 5/16/24.
//

import SwiftUI

struct HideNavigetion_ContentView: View {
    var body: some View {
        TabView {
            HideNavigetion_HomeView()
                .tabItem {
                    Label("Home", systemImage: "house")
                }
            
            HideNavigetion_SettingsView()
                .tabItem {
                    Label("Settings", systemImage: "gear")
                }
        } //: TABVIEW
    }
}

#Preview {
    HideNavigetion_ContentView()
}
