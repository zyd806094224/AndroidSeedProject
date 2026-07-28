Pod::Spec.new do |spec|
    spec.name                = 'shared'
    spec.version             = '1.0.3'
    spec.summary             = 'KMP IM + 业务逻辑共享模块（iOS 二进制版本）'
    spec.homepage            = 'https://github.com/zyd806094224/AndroidSeedProject'
    spec.authors             = { 'zyd806094224' => 'zyd806094224@users.noreply.github.com' }
    spec.license             = {
        :type => 'Proprietary',
        :text => 'Copyright (c) zyd806094224. All rights reserved.'
    }

    spec.source = {
        :http => 'https://github.com/zyd806094224/AndroidSeedProject/releases/download/shared-ios-1.0.3/Shared.xcframework.zip',
        :sha256 => '6b7fa6dbbcf0602399e57ccee261498696a5ae35019e34a26b5aab90b0fa0e5b'
    }

    spec.ios.deployment_target = '12.4'
    spec.module_name           = 'Shared'
    spec.static_framework      = true
    spec.vendored_frameworks   = 'Shared.xcframework'
    spec.libraries             = 'c++'
end
