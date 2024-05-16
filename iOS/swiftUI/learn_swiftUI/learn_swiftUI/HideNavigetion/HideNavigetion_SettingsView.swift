//
//  HideNavigetion_SettingsView.swift
//  learn_swiftUI
//
//  Created by 성준모 on 5/16/24.
//

import SwiftUI

struct HideNavigetion_SettingsView: View {
    @State var isShowingNavigation: Bool = true
    
    
    var body: some View {
        NavigationStack {
            ZStack {
                Color.secondary.opacity(0.2)
                VStack {
                    Image(systemName: "gear")
                        .imageScale(.large)
                        .foregroundStyle(.tint)
                    
                    Text("Settings")
                    
                    Button("Toggle Navigation") {
                        isShowingNavigation.toggle()
                    }
                    .padding()
                    .buttonStyle(.borderedProminent)
                }
                .padding()
                .navigationTitle("Settings")
                
                .toolbar(
                    isShowingNavigation ? .visible : .hidden,
                    for: .navigationBar
                ) //: TOOLBAR
            }
        }
    }
}

#Preview {
    HideNavigetion_SettingsView()
}
